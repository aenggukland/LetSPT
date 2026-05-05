package com.aenggukland.letspt.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Schedule", description = "일정 관리 API — 트레이너/회원 간 PT 일정 요청·수락·수정·취소 (JWT 인증 필수)")
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 조회", description = "본인의 역할(트레이너/회원)에 따라 관련된 모든 일정을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<ScheduleListResponse> getSchedule(@RequestAttribute("username") String username){

        return ResponseEntity.ok().body(scheduleService.getSchedule(username));
    }

    @Operation(summary = "일정 예약 요청 (트레이너)", description = "트레이너가 특정 회원에게 수업 일정을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 요청 생성 성공"),
            @ApiResponse(responseCode = "403", description = "트레이너 권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @PostMapping("/reservation")
    public ResponseEntity<Void> reservation(@RequestAttribute("username") String username, @RequestBody @Valid ScheduleCreateRequest scheduleCreateRequest){
        scheduleService.reservation(username, scheduleCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "예약 수락/거절 (회원)", description = "회원이 트레이너의 일정 요청을 수락하거나 거절합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    @PutMapping("/reply/{scheduleId}")
    public ResponseEntity<Void> replyReservation(
            @RequestAttribute("username") String username,
            @Parameter(description = "일정 ID") @PathVariable Long scheduleId,
            @RequestBody @Valid ScheduleReplyRequest scheduleReplyRequest){
        scheduleService.replyReservation(username, scheduleId, scheduleReplyRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일정 수정 (트레이너)", description = "예약 요청 상태인 수업만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음 또는 상태 불가"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Void> updateReservation(
            @RequestAttribute("username") String username,
            @Parameter(description = "일정 ID") @PathVariable Long scheduleId,
            @RequestBody @Valid ScheduleUpdateRequest scheduleUpdateRequest){
        scheduleService.updateReservation(username, scheduleId, scheduleUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "수업 완료 처리 (트레이너)", description = "수락된 수업을 완료 처리합니다. 활성 횟수권이 있으면 1회 자동 차감됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "완료 처리 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 없음"),
            @ApiResponse(responseCode = "409", description = "COMPLETE 상태 아님")
    })
    @PutMapping("/finish/{scheduleId}")
    public ResponseEntity<Void> finishSchedule(
            @RequestAttribute("username") String username,
            @Parameter(description = "일정 ID") @PathVariable Long scheduleId) {
        scheduleService.finishSchedule(username, scheduleId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "수업 취소 (트레이너)", description = "트레이너가 수업을 취소하고 취소 메모를 남깁니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "403", description = "취소 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    @PutMapping("/cancel/{scheduleId}")
    public ResponseEntity<Void> cancelReservation(
            @RequestAttribute("username") String username,
            @Parameter(description = "일정 ID") @PathVariable Long scheduleId,
            @RequestBody @Valid ScheduleCancelRequest scheduleCancelRequest){
        scheduleService.cancelReservation(username, scheduleId, scheduleCancelRequest);
        return ResponseEntity.ok().build();
    }
}
