package com.aenggukland.letspt.member;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

// 회원 정보 수정 요청 DTO: 모든 필드가 선택적이며 null이 아닌 필드만 업데이트된다
@Getter
public class MemberUpdateRequest {

    @Size(max = 50)
    private String name;

    // 성별: MALE / FEMALE / OTHER 세 가지 값만 허용
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "MALE, FEMALE, OTHER 중 하나여야 합니다.")
    private String gender;

    @Min(1) @Max(150)
    private Integer age;

    // 키: 50.0 ~ 300.0 cm
    @DecimalMin("50.0") @DecimalMax("300.0")
    private BigDecimal height;

    // 몸무게: 10.0 ~ 500.0 kg
    @DecimalMin("10.0") @DecimalMax("500.0")
    private BigDecimal weight;

    // 체지방률: 0.0 ~ 100.0 %
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal bodyFatPercentage;

    // 목표 몸무게: 10.0 ~ 500.0 kg
    @DecimalMin("10.0") @DecimalMax("500.0")
    private BigDecimal targetWeight;

    @Size(max = 255)
    private String fitnessGoal;

    @Pattern(regexp = "^[0-9\\-+() ]{0,20}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;
}
