package com.alzzaipo.domain.member;

import com.alzzaipo.domain.BaseTimeEntity;
import com.alzzaipo.domain.account.local.LocalAccount;
import com.alzzaipo.domain.account.social.SocialAccount;
import com.alzzaipo.domain.notification.criteria.NotificationCriteria;
import com.alzzaipo.domain.notification.email.EmailNotification;
import com.alzzaipo.domain.portfolio.Portfolio;
import com.alzzaipo.enums.LoginType;
import com.alzzaipo.enums.MemberType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private MemberType memberType;

    @Column(nullable = false)
    private String nickname;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Portfolio> portfolios = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private LocalAccount localAccount;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SocialAccount> socialAccounts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<NotificationCriteria> notificationCriteriaList;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private EmailNotification emailNotification;

    @Builder
    public Member(MemberType memberType, String nickname) {
        this.memberType = memberType;
        this.nickname = nickname;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolios.add(portfolio);
        portfolio.setMember(this);
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public SocialAccount getSocialAccount(LoginType loginType) {
        for(SocialAccount account : socialAccounts) {
            if (account.getLoginType() == loginType) {
                return account;
            }
        }
        return null;
    }

    public void addNotificationCriteria(NotificationCriteria notificationCriteria) {
        this.notificationCriteriaList.add(notificationCriteria);
    }
}