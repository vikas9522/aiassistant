package com.vikas.ai_voice_assistant_web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final AIService aiService;

    public HomeController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/ask")
    @ResponseBody
    public String askAI(@RequestBody ChatRequest request) {
        return aiService.getAIResponse(request.getMessage());
    }
}