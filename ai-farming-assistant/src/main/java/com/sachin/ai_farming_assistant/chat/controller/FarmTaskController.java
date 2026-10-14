package com.sachin.ai_farming_assistant.chat.controller;

import com.sachin.ai_farming_assistant.chat.dto.FarmTaskResponse;
import com.sachin.ai_farming_assistant.chat.entity.FarmTask;
import com.sachin.ai_farming_assistant.chat.service.CurrentUserService;
import com.sachin.ai_farming_assistant.chat.service.FarmTaskService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/farm-tasks")
public class FarmTaskController {

    private final FarmTaskService farmTaskService;
    private final CurrentUserService currentUserService;

    public FarmTaskController(
            FarmTaskService farmTaskService,
            CurrentUserService currentUserService
    ) {
        this.farmTaskService = farmTaskService;
        this.currentUserService = currentUserService;
    }

    @PostMapping(
            value = "/{taskId}/approve",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public FarmTaskResponse approveTask(
            @PathVariable Long taskId,
            Authentication authentication
    ) {
        Long userId =
                currentUserService.getAuthenticatedUserId(
                        authentication
                );

        FarmTask task = farmTaskService.approveTask(
                userId,
                taskId
        );

        return FarmTaskResponse.from(task);
    }

    @PostMapping(
            value = "/{taskId}/cancel",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public FarmTaskResponse cancelTask(
            @PathVariable Long taskId,
            Authentication authentication
    ) {
        Long userId =
                currentUserService.getAuthenticatedUserId(
                        authentication
                );

        FarmTask task = farmTaskService.cancelTask(
                userId,
                taskId
        );

        return FarmTaskResponse.from(task);
    }
}