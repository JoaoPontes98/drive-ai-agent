package com.driveai.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiConfig {
    
    @Value("${openai.api-key}")
    private String apiKey;
    
    @Value("${openai.model:gpt-4-turbo}")
    private String model;
    
    @Value("${openai.max-tokens:4000}")
    private Integer maxTokens;
    
    @Value("${openai.temperature:0.7}")
    private Double temperature;
    
    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
    
    public String getModel() {
        return model;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    
    public Double getTemperature() {
        return temperature;
    }
}
