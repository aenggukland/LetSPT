package com.aenggukland.letspt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// IP 기반 Rate Limiting 필터: 브루트포스·대량 가입 공격을 방어한다
// 인증 관련 엔드포인트에만 적용되며, JwtFilter보다 앞에서 실행된다
// 제한: login 10회/분, register 5회/분, refresh 30회/분 (IP당)
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // X-Forwarded-For를 신뢰할 프록시 IP/CIDR 목록 (쉼표 구분)
    // 신뢰 목록 외 remoteAddr에서 온 요청은 헤더를 무시해 스푸핑을 차단한다
    @Value("${app.security.trusted-proxies:127.0.0.1,::1,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16}")
    private String trustedProxiesConfig;

    private List<IpAddressMatcher> trustedProxyMatchers;

    // 경로별 IP → Bucket 매핑 (ConcurrentHashMap으로 스레드 안전 보장)
    private final ConcurrentHashMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> refreshBuckets = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void initTrustedProxyMatchers() {
        trustedProxyMatchers = Arrays.stream(trustedProxiesConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(IpAddressMatcher::new)
                .collect(Collectors.toList());
    }

    // 요청 경로와 IP를 확인해 해당 버킷에서 토큰을 소비한다
    // 토큰 소비 실패 시 429 응답을 반환하고 필터 체인을 중단한다
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = extractIp(request);

        Bucket bucket = resolveBucket(path, ip);

        if (bucket == null) {
            // 제한 대상 경로가 아니면 그냥 통과
            filterChain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume(1)) {
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

    // 경로에 따라 알맞은 버킷 맵에서 IP별 버킷을 반환한다
    // 처음 요청하는 IP라면 새 버킷을 생성해 저장한다
    private Bucket resolveBucket(String path, String ip) {
        if (path.equals("/api/auth/login")) {
            // 로그인: IP당 10회/분
            return loginBuckets.computeIfAbsent(ip, k ->
                    Bucket.builder()
                            .addLimit(Bandwidth.builder()
                                    .capacity(10)
                                    .refillGreedy(10, Duration.ofMinutes(1))
                                    .build())
                            .build());
        }
        if (path.equals("/api/auth/register")) {
            // 회원가입: IP당 5회/분
            return registerBuckets.computeIfAbsent(ip, k ->
                    Bucket.builder()
                            .addLimit(Bandwidth.builder()
                                    .capacity(5)
                                    .refillGreedy(5, Duration.ofMinutes(1))
                                    .build())
                            .build());
        }
        if (path.equals("/api/auth/refresh")) {
            // 토큰 재발급: IP당 30회/분
            return refreshBuckets.computeIfAbsent(ip, k ->
                    Bucket.builder()
                            .addLimit(Bandwidth.builder()
                                    .capacity(30)
                                    .refillGreedy(30, Duration.ofMinutes(1))
                                    .build())
                            .build());
        }
        return null;
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
