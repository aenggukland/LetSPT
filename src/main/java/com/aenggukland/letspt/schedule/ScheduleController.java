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

    @PostMapping("/reservation")
    public ResponseEntity<Void> reservation(@RequestAttribute("username") String username, @RequestBody @Valid ScheduleCreateRequest scheduleCreateRequest){
        scheduleService.reservation(username, scheduleCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/reply/{scheduleId}")
    public ResponseEntity<Void> replyReservation(@RequestAttribute("username") String username, @PathVariable Long scheduleId, @RequestBody @Valid ScheduleReplyRequest scheduleReplyRequest){
        scheduleService.replyReservation(username, scheduleId, scheduleReplyRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Void> updateReservation(@RequestAttribute("username") String username, @PathVariable Long scheduleId, @RequestBody @Valid ScheduleUpdateRequest scheduleUpdateRequest){
        scheduleService.updateReservation(username, scheduleId, scheduleUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
