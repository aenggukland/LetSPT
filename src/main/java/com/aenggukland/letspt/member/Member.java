package com.aenggukland.letspt.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 회원 엔티티: member 테이블과 1:1 매핑되며 MyBatis가 snake_case ↔ camelCase를 자동 변환한다
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private Long memberId;
    private Long roleId;          // MemberRole Enum의 roleId 값 (1=MEMBER, 2=TRAINER, 3=MASTER)
    private String username;
    private String name;
    private String gender;        // MALE / FEMALE / OTHER
    private Integer age;
    private BigDecimal height;    // 단위: cm
    private BigDecimal weight;    // 단위: kg
    private BigDecimal bodyFatPercentage;
    private BigDecimal targetWeight;
    private String fitnessGoal;
    private String phoneNumber;
    private Boolean isDeleted;    // 소프트 삭제 플래그
    private String password;      // BCrypt 암호화된 비밀번호
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime lastLoginAt; // 마지막 로그인 시각 (업데이트 로직 미구현, TODO F1)
    private String profileImageUrl;    // 서버 저장 경로 기반 URL (예: /uploads/profile/uuid.jpg)
}
