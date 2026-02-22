package com.vikas.ai_voice_assistant_web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Map;
import java.util.List;

@Service
public class AIService {

    @Value("${sarvam.api.key}")
    private String apiKey;

    @Value("${sarvam.api.url}")
    private String apiUrl;

    // 🔥 Custom RestTemplate with timeout configuration
    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        return new RestTemplate(factory);
    }

    public String getAIResponse(String userMessage) {

        RestTemplate restTemplate = createRestTemplate();

        try {
            // 🔹 Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 🔹 Request Body
            Map<String, Object> body = Map.of(
                    "model", "sarvam-m",
                    "messages", List.of(
                            Map.of("role", "user", "content", userMessage)
                    )
            );

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            // 🔹 API Call
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(apiUrl, request, Map.class);

            // 🔹 Parse Response
            Map responseBody = response.getBody();

            if (responseBody == null || !responseBody.containsKey("choices")) {
                return "⚠️ Invalid response from Sarvam AI.";
            }

            List choices = (List) responseBody.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map messageObj = (Map) firstChoice.get("message");

            return messageObj.get("content").toString();

        } catch (ResourceAccessException e) {
            return "⚠️ Sarvam AI is taking too long to respond. Please try again.";
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Something went wrong while processing AI response.";
        }
    }
}