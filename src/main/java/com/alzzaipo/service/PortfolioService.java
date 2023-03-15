package com.alzzaipo.service;

import com.alzzaipo.domain.ipo.IPO;
import com.alzzaipo.domain.ipo.IPORepository;
import com.alzzaipo.domain.user.User;
import com.alzzaipo.domain.user.UserRepository;
import com.alzzaipo.domain.portfolio.Portfolio;
import com.alzzaipo.domain.portfolio.PortfolioRepository;
import com.alzzaipo.domain.dto.PortfolioCreateRequestDto;
import com.alzzaipo.domain.dto.PortfolioListDto;
import com.alzzaipo.domain.dto.PortfolioUpdateDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PortfolioService {

    private final EntityManager em;
    private final PortfolioRepository portfolioRepository;
    private final IPORepository ipoRepository;
    private final UserRepository userRepository;

    public Portfolio save(Portfolio portfolio) {
        if(portfolio.getId() == null) {
            em.persist(portfolio);
            return portfolio;
        }
        else {
            return em.merge(portfolio);
        }
    }

    public Portfolio findPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseGet(() -> new Portfolio());

        return portfolio;
    }

    public Boolean createMemberPortfolio(Long memberId, PortfolioCreateRequestDto createRequestDto) {
        IPO ipo = ipoRepository.findByStockCode(createRequestDto.getStockCode())
                .orElseGet(() -> new IPO());

        User user = userRepository.findById(memberId)
                .orElseGet(() -> new User());

        if(ipo.getId() == null) {
            log.warn("Invalid IPO");
            return false;
        }
        else if(user.getId() == null) {
            log.warn("Invalid memberId");
            return false;
        }
        else {
            Portfolio portfolio = Portfolio.builder()
                    .user(user)
                    .ipo(ipo)
                    .sharesCnt(createRequestDto.getSharesCnt())
                    .profit(createRequestDto.getProfit())
                    .agents(createRequestDto.getAgents())
                    .memo(createRequestDto.getMemo())
                    .build();

            user.addPortfolio(portfolio);
            portfolioRepository.save(portfolio);
            return true;
        }
    }

    public Boolean updateMemberPortfolio(Long memberId, PortfolioUpdateDto portfolioUpdateDto) {
        if(portfolioUpdateDto == null) {
            log.error("PortfolioSaveRequestDto is null");
            return false;
        }

        if(portfolioRepository.findById(portfolioUpdateDto.getPortfolioId()).isEmpty()) {
            log.error("Invalid PortfolioId - portfolioUpdateDto.getPortfolioId()" + portfolioUpdateDto.getPortfolioId());
            return false;
        }

        IPO ipo = ipoRepository.findByStockCode(portfolioUpdateDto.getStockCode())
                .orElseGet(() -> new IPO());

        User user = userRepository.findById(memberId)
                .orElseGet(() -> new User());

        if(ipo.getId() == null) {
            log.warn("Invalid IPO");
            return false;
        }
        else if(user.getId() == null) {
            log.warn("Invalid memberId");
            return false;
        }
        else {
            Portfolio portfolio = Portfolio.builder()
                    .user(user)
                    .ipo(ipo)
                    .sharesCnt(portfolioUpdateDto.getSharesCnt())
                    .profit(portfolioUpdateDto.getProfit())
                    .agents(portfolioUpdateDto.getAgents())
                    .memo(portfolioUpdateDto.getMemo())
                    .build();

            portfolio.changeId(portfolioUpdateDto.getPortfolioId());
            save(portfolio);
            return true;
        }
    }

    public boolean deleteMemberPortfolio(Long memberId, Long portfolioId) {
        Portfolio portfolio = findPortfolioById(portfolioId);

        if(portfolio.getId() == null) {
            log.warn("Invalid portfolioId");
            return false;
        }
        else if(memberId.equals(portfolio.getUser().getId())) {
            portfolioRepository.delete(portfolio);
            return true;
        }
        else {
            log.warn("MemberId not match with portfolio owner - memberId:" + memberId + " ownerId:" + portfolio.getUser().getId());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<PortfolioListDto> getMemberPortfolioListDtos(Long memberId) {
        User user = userRepository.findById(memberId)
                .orElseGet(() -> new User());

        if(user.getId() == null) {
            log.warn("Invalid memberId");
            return new ArrayList<>();
        }
        else {
            return user.getPortfolios().stream()
                    .map(Portfolio::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Optional<PortfolioUpdateDto> getPortfolioUpdateDto(Long portfolioId) {
        return portfolioRepository.getPortfolioUpdateDto(portfolioId);
    }
}
