package com.example.codeplatform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class CodeService {

    // 🔥 Add your Gemini API Key via application.properties
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String runCode(String code, String language, String input) {
        String url = "https://emkc.org/api/v2/piston/execute";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("language", mapLanguage(language));
        body.put("version", "*");
        Map<String, String> file = new HashMap<>();
        file.put("name", "Main." + getFileExtension(language));
        file.put("content", code);
        body.put("files", new Map[]{file});
        body.put("stdin", input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            Map<String, Object> run = (Map<String, Object>) response.get("run");
            return (String) run.get("output");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String saveCode(String code, String language) {
        return "Code saved successfully in " + language + "!";
    }

    // 🔥 New method to Review Code with Gemini
    public String reviewCode(String code, String language) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyAwY7RZckusl6IjTx6ZQwnTb9BGjUsxeG0"  ;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request body
        String payload = "{"
                + "\"contents\": ["
                + "  {"
                + "    \"parts\": ["
                + "      {"
                + "        \"text\": \"Review this " + language + " code and suggest improvements:\\n\\n" + code.replace("\"", "\\\"") + "\""
                + "      }"
                + "    ]"
                + "  }"
                + "]"
                + "}";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            // Extract text from response
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("candidates")) {
                Object candidatesObj = responseBody.get("candidates");
                if (candidatesObj instanceof java.util.List) {
                    java.util.List candidatesList = (java.util.List) candidatesObj;
                    if (!candidatesList.isEmpty()) {
                        Map<String, Object> firstCandidate = (Map<String, Object>) candidatesList.get(0);
                        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                        java.util.List parts = (java.util.List) content.get("parts");
                        if (!parts.isEmpty()) {
                            Map<String, Object> part = (Map<String, Object>) parts.get(0);
                            return (String) part.get("text");
                        }
                    }
                }
            }
            return "Failed to extract review from Gemini response.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while reviewing code: " + e.getMessage();
        }
    }

    private String mapLanguage(String language) {
        switch (language.toLowerCase()) {
            case "java": return "java";
            case "python": return "python";
            case "c": return "c";
            case "cpp": return "cpp";
            case "javascript": return "javascript";
            default: return language;
        }
    }

    private String getFileExtension(String language) {
        switch (language.toLowerCase()) {
            case "java": return "java";
            case "python": return "py";
            case "c": return "c";
            case "cpp": return "cpp";
            case "javascript": return "js";
            default: return "txt";
        }
    }
}
