package com.GitSenseAI.PARSER.Controller;

import com.GitSenseAI.PARSER.DTO.ParseRequest;
import com.GitSenseAI.PARSER.DTO.ParseResponse;
import com.GitSenseAI.PARSER.Service.ParserService;
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
@RequestMapping("/api/v1/parser")
public class ParserController {

    private final ParserService parserService;

    @PostMapping("/parse")
    public ResponseEntity<ParseResponse> parse(@RequestBody ParseRequest request) {
        log.info("Received parse request for workspace: {}",
                request.repositoryContext().workspaceInfo().workspacePath());

        ParseResponse response = parserService.parseRepository(request);

        return ResponseEntity.ok(response);
    }
}