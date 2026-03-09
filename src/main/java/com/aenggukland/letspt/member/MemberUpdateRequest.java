package com.aenggukland.letspt.member;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MemberUpdateRequest {
    private String name;
    private String gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bodyFatPercentage;
    private BigDecimal targetWeight;
    private String fitnessGoal;
    private String phoneNumber;
}
