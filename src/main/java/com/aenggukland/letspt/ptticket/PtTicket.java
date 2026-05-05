package com.aenggukland.letspt.ptticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtTicket {
    private Long ticketId;
    private Long trainerId;
    private Long memberId;
    private int totalCount;
    private int remainingCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
