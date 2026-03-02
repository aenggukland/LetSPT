package com.aenggukland.letspt.member;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<Member> findByUsername(String username);

    void save(Member member);
}
