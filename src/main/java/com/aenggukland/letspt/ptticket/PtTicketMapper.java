package com.aenggukland.letspt.ptticket;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PtTicketMapper {

    void save(PtTicket ticket);

    Optional<PtTicket> findById(Long ticketId);

    List<PtTicketResponse> findByMemberId(Long memberId);

    List<PtTicketResponse> findByTrainerId(Long trainerId);

    List<PtTicketResponse> findByMemberAndTrainer(@Param("memberId") Long memberId, @Param("trainerId") Long trainerId);

    Optional<PtTicket> findOldestActiveTicket(@Param("memberId") Long memberId, @Param("trainerId") Long trainerId);

    int deductCount(Long ticketId);

    int deactivate(Long ticketId);
}
