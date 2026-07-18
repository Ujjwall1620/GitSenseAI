package com.GitSenseAI.Bughunter.TEST.controller;



import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionRequest;
import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.TEST.service.TestExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test-execution")
public class TestExecutionController {

    private final TestExecutionService testExecutionService;

    @PostMapping("/run")
    public ResponseEntity<TestExecutionResult> run(@RequestBody TestExecutionRequest request) {
        log.info("Received test execution request for workspace: {}", request.workspacePath());

        TestExecutionResult result = testExecutionService.runTests(request.workspacePath(), request.buildTool());

        return ResponseEntity.ok(result);
    }
}