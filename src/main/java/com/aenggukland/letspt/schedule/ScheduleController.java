package com.aenggukland.letspt.schedule;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 트레이너가 회원에게 일정 공유
// 모든 엔드포인트는 JWT 인증이 필요하며, @RequestAttribute("username")으로 인증 사용자를 수신한다
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 트레이너 -> 사용자 일정 확인 요청
    @PostMapping("/reservation")
    public ResponseEntity<Void> reservation(@RequestAttribute("username") String username, @RequestBody @Valid ScheduleCreateRequest scheduleCreateRequest){
        scheduleService.reservation(username, scheduleCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 회원 -> 트레이너 일정 요청 수락/거절
    @PutMapping("/reply/{scheduleId}")
    public ResponseEntity<Void> replyReservation(@RequestAttribute("username") String username, @PathVariable Long scheduleId, @RequestBody @Valid ScheduleReplyRequest scheduleReplyRequest){
        scheduleService.replyReservation(username, scheduleId, scheduleReplyRequest);
        return ResponseEntity.ok().build();
    }

    // 트레이너가 예약 내용 수정(예약 요청 상태인 수업만 가능)
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Void> updateReservation(@RequestAttribute("username") String username, @PathVariable Long scheduleId, @RequestBody @Valid ScheduleUpdateRequest scheduleUpdateRequest){
        scheduleService.updateReservation(username, scheduleId, scheduleUpdateRequest);
        return ResponseEntity.ok().build();
    }

    // 트레이너가 수업 취소
    @PutMapping("/cancel/{scheduleId}")
    public ResponseEntity<Void> cancelReservation(@RequestAttribute("username") String username, @PathVariable Long scheduleId, @RequestBody @Valid ScheduleCancelRequest scheduleCancelRequest){
        scheduleService.cancelReservation(username, scheduleId, scheduleCancelRequest);
        return ResponseEntity.ok().build();
    }
}
