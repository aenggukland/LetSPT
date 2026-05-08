package com.aenggukland.letspt.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원이 조회할 수 있는 트레이너 공개 프로필 DTO
// 개인 민감 정보(password, 신체 지표)는 제외하고 연락·전문성 정보만 노출한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {
    private Long memberId;
    private String name;
    private String fitnessGoal;       // 트레이너의 전문 분야 또는 지도 철학
    private String phoneNumber;
    private String profileImageUrl;
}
