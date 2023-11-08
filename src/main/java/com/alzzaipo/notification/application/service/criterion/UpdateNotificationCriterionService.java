package com.alzzaipo.notification.application.service.criterion;

import com.alzzaipo.common.Uid;
import com.alzzaipo.member.application.port.out.member.FindMemberPort;
import com.alzzaipo.notification.application.port.dto.UpdateNotificationCriterionCommand;
import com.alzzaipo.notification.application.port.in.criterion.UpdateNotificationCriterionUseCase;
import com.alzzaipo.notification.application.port.out.criterion.FindNotificationCriterionPort;
import com.alzzaipo.notification.application.port.out.criterion.UpdateNotificationCriterionPort;
import com.alzzaipo.notification.domain.criterion.NotificationCriterion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateNotificationCriterionService implements UpdateNotificationCriterionUseCase {

    private final FindMemberPort findMemberPort;
    private final FindNotificationCriterionPort findNotificationCriterionPort;
    private final UpdateNotificationCriterionPort updateNotificationCriterionPort;

    @Override
    public void updateNotificationCriterion(UpdateNotificationCriterionCommand command) {
        validateMemberAndOwnership(command);

        updateNotificationCriterionPort.updateNotificationCriterion(command);
    }

    private void validateMemberAndOwnership(UpdateNotificationCriterionCommand command) {
        Uid memberUID = command.getMemberUID();
        Uid notificationCriterionUID = command.getNotificationCriterionUID();

        findMemberPort.findMember(memberUID)
                .orElseThrow(() -> new RuntimeException("회원 조회 실패"));

        NotificationCriterion notificationCriterion = findNotificationCriterionPort.findNotificationCriterion(notificationCriterionUID)
                .orElseThrow(() -> new RuntimeException("알림 기준 조회 실패"));

        if (!memberUID.equals(notificationCriterion.getMemberUID())) {
            throw new RuntimeException("오류: 접근 권한 없음");
        }
    }
}
