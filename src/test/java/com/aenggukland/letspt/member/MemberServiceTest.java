package com.aenggukland.letspt.member;

import com.aenggukland.letspt.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtProvider = new JwtProvider("test-secret-key-for-junit-test-only!!", 86400000L);
        memberService = new MemberService(memberMapper, passwordEncoder, jwtProvider);
    }

    @Test
    @DisplayName("회원가입 성공")
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest();
        // 리플렉션으로 필드 세팅 (Setter 없으므로)
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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 아이디입니다.");
    }

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰 반환")
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

        String token = memberService.login(request);

        assertThat(token).isNotBlank();
        assertThat(jwtProvider.validateToken(token)).isTrue();
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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
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
