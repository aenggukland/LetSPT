package com.aenggukland.letspt.member;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
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
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteByUsername(rt.getUsername());
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        Member member = memberMapper.findByUsername(rt.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String roleName = MemberRole.fromRoleId(member.getRoleId()).name();
        return jwtProvider.createToken(rt.getUsername(), roleName);
    }

    public void logout(String refreshToken) {
        RefreshToken rt = refreshTokenMapper.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
        refreshTokenMapper.deleteByUsername(rt.getUsername());
    }

    public MemberResponse getMyInfo(String username) {
        Member member = memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    public void updateMyInfo(String username, MemberUpdateRequest request) {
        memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        memberMapper.update(request, username);
    }

    public void changePassword(String username, PasswordChangeRequest request) {
        Member member = memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }
        if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_PASSWORD);
        }

        memberMapper.updatePassword(username, passwordEncoder.encode(request.getNewPassword()));
    }

    public void withdraw(String username) {
        memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        refreshTokenMapper.deleteByUsername(username);
        memberMapper.softDelete(username);
    }
}
