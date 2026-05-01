package com.aenggukland.letspt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// IP 기반 Rate Limiting 필터: 브루트포스·대량 가입 공격을 방어한다
// 인증 관련 엔드포인트에만 적용되며, JwtFilter보다 앞에서 실행된다
// 제한: login 10회/분, register 5회/분, refresh 30회/분 (IP당)
// Redis Lua 스크립트로 INCR+EXPIRE를 원자적으로 실행해 다중 인스턴스 환경에서도 정확한 카운팅 보장
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // X-Forwarded-For를 신뢰할 프록시 IP/CIDR 목록 (쉼표 구분)
    // 신뢰 목록 외 remoteAddr에서 온 요청은 헤더를 무시해 스푸핑을 차단한다
    @Value("${app.security.trusted-proxies:127.0.0.1,::1,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16}")
    private String trustedProxiesConfig;

    private List<IpAddressMatcher> trustedProxyMatchers;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 고정 윈도우(fixed-window) Lua 스크립트:
    // INCR로 카운트 증가 후, 첫 요청일 때만 EXPIRE 설정해 윈도우를 시작한다
    private static final RedisScript<Long> RATE_LIMIT_SCRIPT = RedisScript.of("""
            local count = redis.call('INCR', KEYS[1])
            if count == 1 then
              redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            return count
            """, Long.class);

    private record RateLimitRule(int limit, int windowSec) {}

    private static final Map<String, RateLimitRule> RULES = Map.of(
            "/api/auth/login",    new RateLimitRule(10, 60),
            "/api/auth/register", new RateLimitRule(5,  60),
            "/api/auth/refresh",  new RateLimitRule(30, 60)
    );

    public RateLimitFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void initTrustedProxyMatchers() {
        trustedProxyMatchers = Arrays.stream(trustedProxiesConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(IpAddressMatcher::new)
                .collect(Collectors.toList());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        RateLimitRule rule = RULES.get(path);

        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = extractIp(request);
        String key = "rl:" + path + ":" + ip;

        if (isAllowed(key, rule)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(),
                    Map.of("code", "RATE_LIMIT_EXCEEDED",
                           "message", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."));
        }
    }

    private boolean isAllowed(String key, RateLimitRule rule) {
        try {
            Long count = redisTemplate.execute(
                    RATE_LIMIT_SCRIPT,
                    List.of(key),
                    String.valueOf(rule.windowSec())
            );
            return count != null && count <= rule.limit();
        } catch (Exception e) {
            // Redis 장애 시 fail-open: 서비스 가용성 우선, 경고 로그만 출력
            logger.warn("Redis rate limit unavailable, fail-open: " + e.getMessage());
            return true;
        }
    }

    // 클라이언트 IP 추출: 신뢰된 프록시에서 온 요청에만 X-Forwarded-For를 사용한다
    // 신뢰 목록 외 출처의 헤더는 무시해 IP 스푸핑으로 Rate Limit 우회하는 것을 차단한다
    private String extractIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        if (isTrustedProxy(remoteAddr)) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                // X-Forwarded-For는 "실제IP, 프록시IP, ..." 형태이므로 첫 번째 값이 원본 IP
                return forwarded.split(",")[0].trim();
            }
        }
        return remoteAddr;
    }

    private boolean isTrustedProxy(String ip) {
        return trustedProxyMatchers.stream().anyMatch(matcher -> matcher.matches(ip));
    }
}
