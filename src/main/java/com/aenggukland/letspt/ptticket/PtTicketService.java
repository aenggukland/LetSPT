package com.aenggukland.letspt.ptticket;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.member.Member;
import com.aenggukland.letspt.member.MemberMapper;
import com.aenggukland.letspt.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PtTicketService {

    private final PtTicketMapper ptTicketMapper;
    private final MemberMapper memberMapper;

    public void registerTicket(String trainerUsername, PtTicketCreateRequest request) {
        Member trainer = memberMapper.findByUsername(trainerUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        MemberRole role = MemberRole.fromRoleId(trainer.getRoleId());
        if (role != MemberRole.TRAINER && role != MemberRole.MASTER) {
            throw new BusinessException(ErrorCode.PT_TICKET_ACCESS_DENIED);
        }

        Member member = memberMapper.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (MemberRole.fromRoleId(member.getRoleId()) != MemberRole.MEMBER) {
            throw new BusinessException(ErrorCode.PT_TICKET_ACCESS_DENIED);
        }

        PtTicket ticket = PtTicket.builder()
                .trainerId(trainer.getMemberId())
                .memberId(request.getMemberId())
                .totalCount(request.getTotalCount())
                .remainingCount(request.getTotalCount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();

        ptTicketMapper.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<PtTicketResponse> getMyTickets(String memberUsername) {
        Member member = memberMapper.findByUsername(memberUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return ptTicketMapper.findByMemberId(member.getMemberId());
    }

    @Transactional(readOnly = true)
    public List<PtTicketResponse> getTrainerTickets(String trainerUsername) {
        Member trainer = memberMapper.findByUsername(trainerUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return ptTicketMapper.findByTrainerId(trainer.getMemberId());
    }

    @Transactional(readOnly = true)
    public List<PtTicketResponse> getMemberTickets(String trainerUsername, Long memberId) {
        Member trainer = memberMapper.findByUsername(trainerUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        memberMapper.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return ptTicketMapper.findByMemberAndTrainer(memberId, trainer.getMemberId());
    }

    public void deactivateTicket(String trainerUsername, Long ticketId) {
        Member trainer = memberMapper.findByUsername(trainerUsername)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        PtTicket ticket = ptTicketMapper.findById(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PT_TICKET_NOT_FOUND));

        if (!ticket.getTrainerId().equals(trainer.getMemberId())) {
            throw new BusinessException(ErrorCode.PT_TICKET_ACCESS_DENIED);
        }

        if (!ticket.isActive()) {
            throw new BusinessException(ErrorCode.PT_TICKET_ALREADY_INACTIVE);
        }

        ptTicketMapper.deactivate(ticketId);
    }

    // 수업 완료(FINISH) 처리 시 ScheduleService에서 호출: 활성 횟수권이 있으면 1회 차감
    public void deductActiveTicket(Long memberId, Long trainerId) {
        ptTicketMapper.findOldestActiveTicket(memberId, trainerId)
                .ifPresent(ticket -> ptTicketMapper.deductCount(ticket.getTicketId()));
    }
}
