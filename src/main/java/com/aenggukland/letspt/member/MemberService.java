package com.aenggukland.letspt.member;

import com.aenggukland.letspt.security.JwtProvider;
import com.aenggukland.letspt.security.RefreshToken;
import com.aenggukland.letspt.security.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenMapper refreshTokenMapper;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    public void register(RegisterRequest request) {
        if (memberMapper.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member member = Member.builder()
                .roleId(MemberRole.MEMBER.getRoleId())
                .username(request.getUsername())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberMapper.save(member);
    }

    public Map<String, String> login(LoginRequest request) {
        Member member = memberMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String roleName = MemberRole.fromRoleId(member.getRoleId()).name();
        String accessToken = jwtProvider.createToken(member.getUsername(), roleName);
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenMapper.save(RefreshToken.builder()
                .username(member.getUsername())
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .build());

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public String refresh(String refreshToken) {
        RefreshToken rt = refreshTokenMapper.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteByUsername(rt.getUsername());
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
        }

        Member member = memberMapper.findByUsername(rt.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String roleName = MemberRole.fromRoleId(member.getRoleId()).name();
        return jwtProvider.createToken(rt.getUsername(), roleName);
    }

    public void logout(String refreshToken) {
        RefreshToken rt = refreshTokenMapper.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));
        refreshTokenMapper.deleteByUsername(rt.getUsername());
    }
}
