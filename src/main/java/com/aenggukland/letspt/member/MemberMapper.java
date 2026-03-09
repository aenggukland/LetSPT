package com.aenggukland.letspt.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    Optional<Member> findByUsername(String username);

    Optional<Member> findById(Long memberId);

    void save(Member member);

    void update(@Param("request") MemberUpdateRequest request, @Param("username") String username);

    void updatePassword(@Param("username") String username, @Param("newEncodedPassword") String newEncodedPassword);

    void updateProfileImage(@Param("username") String username,
                            @Param("profileImageUrl") String profileImageUrl);

    void softDelete(String username);
}
