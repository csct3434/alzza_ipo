package com.alzzaipo.hexagonal.member.application.port.out;

import com.alzzaipo.hexagonal.common.Uid;

public interface ChangeMemberNicknamePort {

    void changeMemberNickname(Uid memberUID, String nickname);
}
