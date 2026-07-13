package com.GitSenseAI.REPOSITORY.DTO;

import java.time.Instant;

public record WorkspaceInfo(String workspaceId,
                            String workspacePath,
                            Instant createdAt) {
}
