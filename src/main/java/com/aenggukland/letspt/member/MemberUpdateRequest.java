package com.aenggukland.letspt.member;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MemberUpdateRequest {

    @Size(max = 50)
    private String name;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "MALE, FEMALE, OTHER 중 하나여야 합니다.")
    private String gender;

    @Min(1) @Max(150)
    private Integer age;

    @DecimalMin("50.0") @DecimalMax("300.0")
    private BigDecimal height;

    @DecimalMin("10.0") @DecimalMax("500.0")
    private BigDecimal weight;

    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal bodyFatPercentage;

    @DecimalMin("10.0") @DecimalMax("500.0")
    private BigDecimal targetWeight;

    @Size(max = 255)
    private String fitnessGoal;

    @Pattern(regexp = "^[0-9\\-+() ]{0,20}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;
}
