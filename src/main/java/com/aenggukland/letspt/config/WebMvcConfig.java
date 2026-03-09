package com.aenggukland.letspt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(profileUploadDir).toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
