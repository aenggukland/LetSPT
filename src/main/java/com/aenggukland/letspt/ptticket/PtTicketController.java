package com.aenggukland.letspt.ptticket;

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

@Tag(name = "PtTicket", description = "PT 횟수권 관리 API — 트레이너의 횟수권 등록·조회·비활성화 (JWT 인증 필수)")
@RestController
@RequiredArgsConstructor
public class PtTicketController {

    private final PtTicketService ptTicketService;

    @Operation(summary = "횟수권 등록 (트레이너)", description = "트레이너가 담당 회원에게 PT 횟수권을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 또는 대상이 회원이 아님"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @PostMapping("/api/admin/pt-ticket")
    public ResponseEntity<Void> registerTicket(
            @RequestAttribute("username") String username,
            @RequestBody @Valid PtTicketCreateRequest request) {
        ptTicketService.registerTicket(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "담당 회원 횟수권 전체 조회 (트레이너)", description = "트레이너가 등록한 모든 회원의 횟수권 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/admin/pt-ticket")
    public ResponseEntity<List<PtTicketResponse>> getTrainerTickets(
            @RequestAttribute("username") String username) {
        return ResponseEntity.ok(ptTicketService.getTrainerTickets(username));
    }

    @Operation(summary = "특정 회원 횟수권 조회 (트레이너)", description = "트레이너가 특정 회원의 횟수권 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @GetMapping("/api/admin/pt-ticket/member/{memberId}")
    public ResponseEntity<List<PtTicketResponse>> getMemberTickets(
            @RequestAttribute("username") String username,
            @Parameter(description = "회원 ID") @PathVariable Long memberId) {
        return ResponseEntity.ok(ptTicketService.getMemberTickets(username, memberId));
    }

    @Operation(summary = "내 횟수권 조회 (회원)", description = "회원이 자신의 PT 횟수권 전체 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/member/pt-ticket")
    public ResponseEntity<List<PtTicketResponse>> getMyTickets(
            @RequestAttribute("username") String username) {
        return ResponseEntity.ok(ptTicketService.getMyTickets(username));
    }

    @Operation(summary = "횟수권 비활성화 (트레이너)", description = "트레이너가 본인이 등록한 횟수권을 수동으로 비활성화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비활성화 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "횟수권 없음")
    })
    @DeleteMapping("/api/admin/pt-ticket/{ticketId}")
    public ResponseEntity<Void> deactivateTicket(
            @RequestAttribute("username") String username,
            @Parameter(description = "횟수권 ID") @PathVariable Long ticketId) {
        ptTicketService.deactivateTicket(username, ticketId);
        return ResponseEntity.noContent().build();
    }
}
