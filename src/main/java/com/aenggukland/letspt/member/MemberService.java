package com.aenggukland.letspt.member;

import com.aenggukland.letspt.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void register(RegisterRequest request) {
        if (memberMapper.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member member = Member.builder()
                .roleId(MemberRole.MEMBER.getRoleId())   // role 테이블의 MEMBER row id
                .username(request.getUsername())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberMapper.save(member);
    }

    public String login(LoginRequest request) {
        Member member = memberMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // roleId → role 이름 변환
        String roleName = MemberRole.fromRoleId(member.getRoleId()).name();
        return jwtProvider.createToken(member.getUsername(), roleName);
    }
}
