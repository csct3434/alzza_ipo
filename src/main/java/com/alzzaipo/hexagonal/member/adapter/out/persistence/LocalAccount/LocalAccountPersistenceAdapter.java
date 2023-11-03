package com.alzzaipo.hexagonal.member.adapter.out.persistence.LocalAccount;

import com.alzzaipo.hexagonal.common.Email;
import com.alzzaipo.hexagonal.member.adapter.out.persistence.Member.MemberJpaEntity;
import com.alzzaipo.hexagonal.member.adapter.out.persistence.Member.NewMemberRepository;
import com.alzzaipo.hexagonal.member.application.port.out.FindLocalAccountByAccountIdPort;
import com.alzzaipo.hexagonal.member.application.port.out.FindLocalAccountByEmailPort;
import com.alzzaipo.hexagonal.member.application.port.out.RegisterLocalAccountPort;
import com.alzzaipo.hexagonal.member.domain.LocalAccount.LocalAccount;
import com.alzzaipo.hexagonal.member.domain.LocalAccount.LocalAccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class LocalAccountPersistenceAdapter implements
        FindLocalAccountByAccountIdPort,
        FindLocalAccountByEmailPort,
        RegisterLocalAccountPort {

    private final NewLocalAccountRepository localAccountRepository;
    private final NewMemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalAccount> findLocalAccountByAccountId(LocalAccountId localAccountId) {
        return localAccountRepository.findByAccountId(localAccountId.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LocalAccount> findLocalAccountByEmailPort(Email email) {
        return localAccountRepository.findByEmail(email.get());
    }

    @Override
    public void registerLocalAccountPort(LocalAccount localAccount) {
        MemberJpaEntity memberJpaEntity = memberRepository.findByUid(localAccount.getMemberUID().get())
                .orElseThrow(() -> new IllegalArgumentException("회원 엔티티 조회 실패"));

        LocalAccountJpaEntity localAccountJpaEntity = new LocalAccountJpaEntity(
                localAccount.getAccountId().get(),
                localAccount.getAccountPassword().get(),
                localAccount.getEmail().get(),
                memberJpaEntity
        );

        localAccountRepository.save(localAccountJpaEntity);
    }
}
