package com.aenggukland.letspt.member;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import com.aenggukland.letspt.security.JwtProvider;
import com.aenggukland.letspt.security.RefreshToken;
import com.aenggukland.letspt.security.RefreshTokenMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private RefreshTokenMapper refreshTokenMapper;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtProvider = new JwtProvider("test-secret-key-for-junit-test-only!!", 1800000L);
        memberService = new MemberService(memberMapper, passwordEncoder, jwtProvider, refreshTokenMapper, redisTemplate);
        ReflectionTestUtils.setField(memberService, "refreshExpirationMs", 604800000L);
    }

    // =====================
    // 회원가입
    // =====================

    @Test
    @DisplayName("회원가입 성공")
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest();
        setField(request, "username", "testuser");
        setField(request, "password", "password123");
        setField(request, "name", "홍길동");

        when(memberMapper.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> memberService.register(request));
        verify(memberMapper).save(any(Member.class));
    }

    @Test
    @DisplayName("중복 아이디로 회원가입 시 예외 발생")
    void registerDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        setField(request, "username", "testuser");
        setField(request, "password", "password123");
        setField(request, "name", "홍길동");

        when(memberMapper.findByUsername("testuser"))
                .thenReturn(Optional.of(Member.builder().username("testuser").build()));

        assertThatThrownBy(() -> memberService.register(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.DUPLICATE_USERNAME));
    }

    // =====================
    // 로그인
    // =====================

    @Test
    @DisplayName("로그인 성공 시 accessToken과 refreshToken 반환")
    void loginSuccess() {
        String rawPassword = "password123";
        Member member = Member.builder()
                .username("testuser")
                .password(passwordEncoder.encode(rawPassword))
                .roleId(MemberRole.MEMBER.getRoleId())
                .build();

        LoginRequest request = new LoginRequest();
        setField(request, "username", "testuser");
        setField(request, "password", rawPassword);

        when(memberMapper.findByUsername("testuser")).thenReturn(Optional.of(member));

        Map<String, String> result = memberService.login(request);

        assertThat(result).containsKeys("accessToken", "refreshToken");
        assertThat(jwtProvider.validateToken(result.get("accessToken"))).isTrue();
        assertThat(result.get("refreshToken")).isNotBlank();
        verify(refreshTokenMapper).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시 예외 발생")
    void loginNotFound() {
        LoginRequest request = new LoginRequest();
        setField(request, "username", "unknown");
        setField(request, "password", "password123");

        when(memberMapper.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void loginWrongPassword() {
        Member member = Member.builder()
                .username("testuser")
                .password(passwordEncoder.encode("correct"))
                .roleId(MemberRole.MEMBER.getRoleId())
                .build();

        LoginRequest request = new LoginRequest();
        setField(request, "username", "testuser");
        setField(request, "password", "wrong");

        when(memberMapper.findByUsername("testuser")).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_PASSWORD));
    }

    // =====================
    // Access Token 재발급
    // =====================

    @Test
    @DisplayName("유효한 Refresh Token으로 Access Token 재발급 성공")
    void refreshSuccess() {
        Member member = Member.builder()
                .username("testuser")
                .roleId(MemberRole.MEMBER.getRoleId())
                .build();

        RefreshToken rt = RefreshToken.builder()
                .username("testuser")
                .token("valid-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenMapper.findByToken("valid-refresh-token")).thenReturn(Optional.of(rt));
        when(memberMapper.findByUsername("testuser")).thenReturn(Optional.of(member));

        String newAccessToken = memberService.refresh("valid-refresh-token");

        assertThat(newAccessToken).isNotBlank();
        assertThat(jwtProvider.validateToken(newAccessToken)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 Refresh Token으로 재발급 시 예외 발생")
    void refreshInvalidToken() {
        when(refreshTokenMapper.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.refresh("invalid-token"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_TOKEN));
    }

    @Test
    @DisplayName("만료된 Refresh Token으로 재발급 시 예외 발생 및 토큰 삭제")
    void refreshExpiredToken() {
        RefreshToken rt = RefreshToken.builder()
                .username("testuser")
                .token("expired-refresh-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(refreshTokenMapper.findByToken("expired-refresh-token")).thenReturn(Optional.of(rt));

        assertThatThrownBy(() -> memberService.refresh("expired-refresh-token"))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.EXPIRED_TOKEN));

        verify(refreshTokenMapper).deleteByUsername("testuser");
    }

    // =====================
    // 로그아웃
    // =====================

    @Test
    @DisplayName("로그아웃 성공 시 Refresh Token 삭제")
    void logoutSuccess() {
        RefreshToken rt = RefreshToken.builder()
                .username("testuser")
                .token("valid-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        String accessToken = jwtProvider.createToken("testuser", "MEMBER");
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(refreshTokenMapper.findByToken("valid-refresh-token")).thenReturn(Optional.of(rt));

        assertThatNoException().isThrownBy(() -> memberService.logout("valid-refresh-token", accessToken));
        verify(refreshTokenMapper).deleteByUsername("testuser");
    }

    @Test
    @DisplayName("유효하지 않은 Refresh Token으로 로그아웃 시 예외 발생")
    void logoutInvalidToken() {
        when(refreshTokenMapper.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.logout("invalid-token", null))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_TOKEN));
    }

    // Lombok @Getter만 있으므로 테스트에서 리플렉션으로 필드 세팅
    private void setField(Object target, String fieldName, String value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
