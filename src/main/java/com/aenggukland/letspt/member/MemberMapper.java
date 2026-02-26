package com.aenggukland.letspt.member;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    @Select("SELECT * FROM member WHERE username = #{username} AND is_deleted = FALSE")
    Optional<Member> findByUsername(String username);

    @Insert("INSERT INTO member (role_id, username, name, password) " +
            "VALUES (#{roleId}, #{username}, #{name}, #{password})")
    void save(Member member);
}
