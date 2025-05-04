package com.example.codeplatform.controller;

import com.example.codeplatform.model.CodeRequest;
import com.example.codeplatform.model.CodeResponse;
import com.example.codeplatform.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CodeController {

    @Autowired
    private CodeService codeService;

    @PostMapping("/run")
    public String runCode(@RequestBody CodeRequest request) {
        String code = request.getCode();
        String language = request.getLanguage();
        String input = request.getInput();

        String output = codeService.runCode(code, language, input);

        return output;
    }

    @PostMapping("/save")
    public CodeResponse saveCode(@RequestBody CodeRequest request) {
        String output = codeService.saveCode(request.getCode(), request.getLanguage());
        return new CodeResponse(output);
    }

    // 🔥 NEW: Review code using Gemini
    @PostMapping("/review")
    public CodeResponse reviewCode(@RequestBody CodeRequest request) {
        String reviewOutput = codeService.reviewCode(request.getCode(), request.getLanguage());
        return new CodeResponse(reviewOutput);
    }
}
