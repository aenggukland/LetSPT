package com.aenggukland.letspt.ptticket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PtTicketCreateRequest {

    @NotNull(message = "회원 ID는 필수입니다.")
    @Schema(description = "대상 회원 ID", example = "5")
    private Long memberId;

    @NotNull(message = "횟수는 필수입니다.")
    @Min(value = 1, message = "횟수는 1 이상이어야 합니다.")
    @Max(value = 999, message = "횟수는 999 이하여야 합니다.")
    @Schema(description = "등록 횟수", example = "30")
    private Integer totalCount;

    @NotNull(message = "시작일은 필수입니다.")
    @Schema(description = "횟수권 시작일", example = "2026-05-01")
    private LocalDate startDate;

    @Schema(description = "횟수권 만료일 (선택)", example = "2026-11-01")
    private LocalDate endDate;
}
