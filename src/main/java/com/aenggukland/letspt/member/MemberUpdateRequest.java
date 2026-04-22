package com.aenggukland.letspt.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.math.BigDecimal;

@Schema(description = "회원 정보 수정 요청 (모든 필드 선택적, null이 아닌 필드만 업데이트)")
@Getter
public class MemberUpdateRequest {

    @Schema(description = "이름 (최대 50자)", example = "홍길동")
    @Size(max = 50)
    private String name;

    @Schema(description = "성별 (MALE / FEMALE / OTHER)", example = "MALE")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "MALE, FEMALE, OTHER 중 하나여야 합니다.")
    private String gender;

    @Schema(description = "나이 (1~150)", example = "25")
    @Min(1) @Max(150)
    private Integer age;

    @Schema(description = "키 cm (50.0~300.0)", example = "175.5")
    @DecimalMin("50.0") @DecimalMax("300.0")
    private BigDecimal height;

    @Schema(description = "몸무게 kg (10.0~500.0)", example = "70.0")
    @DecimalMin("10.0") @DecimalMax("500.0")
    private BigDecimal weight;

    @Schema(description = "체지방률 % (0.0~100.0)", example = "18.5")
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal bodyFatPercentage;

    @Schema(description = "목표 몸무게 kg (10.0~500.0)", example = "65.0")
    @DecimalMin("10.0") @DecimalMax("500.0")
    private BigDecimal targetWeight;

    @Schema(description = "운동 목표 (최대 255자)", example = "체중 감량 및 근육 증가")
    @Size(max = 255)
    private String fitnessGoal;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @Pattern(regexp = "^[0-9\\-+() ]{0,20}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;
}
