package com.aenggukland.letspt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

// MVC 설정: 서버 로컬 파일시스템에 저장된 프로필 이미지를 정적 리소스로 서빙한다
// /uploads/profile/** URL로 접근 시 UPLOAD_DIR 경로의 실제 파일을 반환한다
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 프로필 이미지 저장 디렉터리 (기본값: uploads/profile)
    @Value("${upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    // /uploads/profile/** 요청을 로컬 파일시스템 경로로 매핑
    // 절대 경로로 변환해 OS에 관계없이 동작하도록 한다
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(profileUploadDir).toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
