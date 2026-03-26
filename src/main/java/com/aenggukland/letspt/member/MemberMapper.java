package com.aenggukland.letspt.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

// 회원 데이터 접근 Mapper: SQL은 MemberMapper.xml에만 작성한다
@Mapper
public interface MemberMapper {

    // username으로 단건 조회 (삭제된 회원 제외)
    Optional<Member> findByUsername(String username);

    // memberId로 단건 조회 (삭제된 회원 제외)
    Optional<Member> findById(Long memberId);

    // 새 회원 저장
    void save(Member member);

    // 회원 정보 부분 수정: null이 아닌 필드만 업데이트 (<if> 활용)
    void update(@Param("request") MemberUpdateRequest request, @Param("username") String username);

    // 비밀번호 변경: BCrypt로 암호화된 값을 직접 받아 저장한다
    void updatePassword(@Param("username") String username, @Param("newEncodedPassword") String newEncodedPassword);

    // 프로필 이미지 URL 업데이트 (null 전달 시 삭제 처리)
    void updateProfileImage(@Param("username") String username,
                            @Param("profileImageUrl") String profileImageUrl);

    // 소프트 삭제: is_deleted = TRUE, deleted_at = CURRENT_TIMESTAMP 설정
    void softDelete(String username);
}
