package com.aenggukland.letspt.chatbot;

import com.aenggukland.letspt.exception.BusinessException;
import com.aenggukland.letspt.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

// 외부 AI API 호출 서비스: OpenAI Chat Completions 호환 API를 사용한다
// API URL·키·모델은 환경변수로 주입받아 특정 AI 공급자에 종속되지 않는다
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String aiApiUrl;

    @Value("${ai.api.key}")
    private String aiApiKey;

    @Value("${ai.api.model}")
    private String aiModel;

    private static final String SYSTEM_PROMPT =
            "당신은 PT(Personal Training) 전문 AI 어시스턴트입니다. " +
            "회원들의 운동, 식단, 건강 관련 질문에 친절하고 전문적으로 답변해주세요. " +
            "의학적 진단이나 처방은 제공하지 않으며, 필요한 경우 전문 의료인 상담을 권장합니다. " +
            "답변은 한국어로 제공하며, 과학적 근거에 기반한 정보를 제공합니다.";

    // 사용자 메시지를 AI API에 전달하고 응답을 반환한다
    // 시스템 프롬프트는 PT 코칭 역할로 고정하며 대화 컨텍스트는 유지하지 않는다(단일 턴)
    public ChatbotResponse chat(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiApiKey);

        AiApiRequest requestBody = new AiApiRequest(
                aiModel,
                List.of(
                        new AiChatMessage("system", SYSTEM_PROMPT),
                        new AiChatMessage("user", userMessage)
                )
        );

        HttpEntity<AiApiRequest> httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            Map<?, ?> response = restTemplate.postForObject(aiApiUrl, httpEntity, Map.class);
            String answer = extractContent(response);
            return new ChatbotResponse(answer);
        } catch (RestClientException e) {
            log.warn("AI API 호출 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_API_CALL_FAILED);
        }
    }

    // OpenAI Chat Completions 응답에서 텍스트를 추출한다
    // 응답 형식: choices[0].message.content
    @SuppressWarnings("unchecked")
    private String extractContent(Map<?, ?> response) {
        try {
            List<?> choices = (List<?>) response.get("choices");
            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) choice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.warn("AI API 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_API_CALL_FAILED);
        }
    }
}
