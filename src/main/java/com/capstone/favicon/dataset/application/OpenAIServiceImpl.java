package com.capstone.favicon.dataset.application;

import com.capstone.favicon.dataset.application.service.OpenAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Override
    public String chat(List<Map<String, String>> messages) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey).build();
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", messages,
                "temperature", 0.2,
                "max_tokens", 400
        );

        return client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
