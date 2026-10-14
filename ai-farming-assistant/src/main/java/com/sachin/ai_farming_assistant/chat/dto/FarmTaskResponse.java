package com.sachin.ai_farming_assistant.chat.dto;

import com.sachin.ai_farming_assistant.chat.entity.FarmTask;

import java.time.LocalDate;

public record FarmTaskResponse(
        Long taskId,
        Long farmId,
        String title,
        String description,
        LocalDate dueDate,
        String priority,
        String status
) {

    public static FarmTaskResponse from(FarmTask task) {
        return new FarmTaskResponse(
                task.getId(),
                task.getFarmId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority().name(),
                task.getStatus().name()
        );
    }
}