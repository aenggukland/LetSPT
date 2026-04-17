package com.aenggukland.letspt.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

// Firebase Admin SDK 초기화 설정
// 서버 재시작 시 이미 초기화된 앱이 있으면 새로 만들지 않고 기존 인스턴스를 반환한다
@Configuration
public class FirebaseConfig {

    // Firebase 서비스 계정 JSON 파일 경로 (application.yml의 firebase.service-account-path)
    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;

    // FirebaseApp 빈 등록: 중복 초기화 방지를 위해 이미 앱이 등록된 경우 기존 인스턴스를 반환한다
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance(); // 서버 재시작 없이 컨텍스트가 재로드될 때 중복 방지
        }

        FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        return FirebaseApp.initializeApp(options);
    }
}