package com.aenggukland.letspt.member;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.security.JwtProvider;
import com.aenggukland.letspt.security.RefreshToken;
import com.aenggukland.letspt.security.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 회원 관련 비즈니스 로직을 처리하는 서비스
// 인증(로그인/로그아웃/토큰 재발급), 프로필 관리, 비밀번호 변경, 회원 탈퇴를 담당한다
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenMapper refreshTokenMapper;
    private final RedisTemplate<String, String> redisTemplate;

    // Refresh Token 유효기간 (기본값: 7일, 밀리초 단위)
    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    // 프로필 이미지 저장 디렉터리 경로
    @Value("${upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    // 회원가입: 중복 아이디 검증 후 BCrypt로 비밀번호를 암호화해 MEMBER 역할로 저장한다
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

    // 로그인: 비밀번호 검증 후 AccessToken(JWT)과 RefreshToken(UUID)을 발급한다
    // RefreshToken은 DB에 저장되며, 동일 username으로 재로그인 시 덮어쓴다
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

    // Access Token 재발급: Refresh Token 유효성(존재 여부, 만료 여부)을 검증하고 새 토큰을 반환한다
    // 만료된 경우 DB에서 토큰을 삭제한 뒤 예외를 던진다
    public String refresh(String refreshToken) {
        RefreshToken rt = refreshTokenMapper.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteByUsername(rt.getUsername()); // 만료된 토큰 즉시 삭제
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        Member member = memberMapper.findByUsername(rt.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String roleName = MemberRole.fromRoleId(member.getRoleId()).name();
        return jwtProvider.createToken(rt.getUsername(), roleName);
    }

    // 로그아웃: DB에서 Refresh Token을 삭제해 재사용을 방지한다
    public void logout(String refreshToken, String accessToken) {
        RefreshToken rt = refreshTokenMapper.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        // accessToken이 있을 때만 블랙리스트 등록
        if (accessToken != null) {
            long expirationTime = jwtProvider.getRemainingExpiration(accessToken);
            redisTemplate.opsForValue().set("blacklist:" + accessToken, "logout", expirationTime, TimeUnit.MILLISECONDS);
        }

        refreshTokenMapper.deleteByUsername(rt.getUsername());
    }

    // 내 정보 조회: username으로 회원을 조회하고 MemberResponse DTO로 변환해 반환한다
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(String username) {
        Member member = memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    // 내 정보 수정: 회원 존재 확인 후 null이 아닌 필드만 선택적으로 업데이트한다
    public void updateMyInfo(String username, MemberUpdateRequest request) {
        memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        memberMapper.update(request, username);
    }

    // 비밀번호 변경: 현재 비밀번호 일치 확인 → 새 비밀번호 동일 여부 확인 → 암호화 후 저장
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

    // 프로필 이미지 업로드: MIME 타입 검증 후 UUID 파일명으로 서버에 저장하고 URL을 DB에 기록한다
    // MIME 타입 기반 검증만 수행하며 Magic Bytes 검증은 미구현 (TODO S8)
    public String uploadProfileImage(String username, MultipartFile file) {
        memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String savedFileName = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path savePath = Paths.get(profileUploadDir, savedFileName).toAbsolutePath();

        try {
            Files.createDirectories(savePath.getParent());
            Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        String imageUrl = "/uploads/profile/" + savedFileName;
        memberMapper.updateProfileImage(username, imageUrl);
        return imageUrl;
    }

    // 프로필 이미지 삭제: DB의 URL만 null로 초기화하며 서버 파일은 삭제하지 않는다 (TODO B5)
    public void deleteProfileImage(String username) {
        memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        memberMapper.updateProfileImage(username, null);
    }

    // 회원 탈퇴: Refresh Token 삭제 후 소프트 삭제(is_deleted = TRUE) 처리한다
    // 프로필 이미지 파일은 물리 삭제되지 않는다 (TODO B6)
    public void withdraw(String username) {
        memberMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        refreshTokenMapper.deleteByUsername(username);
        memberMapper.softDelete(username); // 물리 삭제 대신 소프트 삭제
    }
}
