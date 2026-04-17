package com.aenggukland.letspt.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// Kafka 메시지 발행 서비스: WebSocket으로 수신된 채팅 메시지를 "chat" 토픽으로 전달한다
// 분산 환경에서도 메시지 순서를 보장하고 다수의 인스턴스에 메시지를 전달할 수 있다
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // "chat" 토픽으로 메시지를 비동기 발행한다
    public void sendMessage(String message) {
        kafkaTemplate.send("chat", message);
    }
}