package com.sachin.ai_farming_assistant.chat.service;

import com.sachin.ai_farming_assistant.chat.entity.Farm;
import com.sachin.ai_farming_assistant.chat.entity.FarmTask;
import com.sachin.ai_farming_assistant.chat.entity.TaskPriority;

import com.sachin.ai_farming_assistant.chat.exception.FarmNotFoundException;
import com.sachin.ai_farming_assistant.chat.repository.FarmRepository;
import com.sachin.ai_farming_assistant.chat.repository.FarmTaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class FarmTaskService {

    private final FarmTaskRepository farmTaskRepository;
    private final FarmRepository farmRepository;

    public FarmTaskService(
            FarmTaskRepository farmTaskRepository,
            FarmRepository farmRepository
    ) {
        this.farmTaskRepository = farmTaskRepository;
        this.farmRepository = farmRepository;
    }

    @Transactional
    public FarmTask createFarmTask(
            Long authenticatedUserId,
            Long requestedFarmId,
            String title,
            String description,
            String dueDate,
            String priority
    ) {
        // 1. Validate authenticated user
        if (authenticatedUserId == null
                || authenticatedUserId <= 0) {
            throw new IllegalArgumentException(
                    "Authenticated user is required"
            );
        }

        // 2. Validate title
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Task title is required"
            );
        }

        title = title.trim();

        if (title.length() > 120) {
            throw new IllegalArgumentException(
                    "Task title cannot exceed 120 characters"
            );
        }

        // 3. Validate description
        if (description != null) {
            description = description.trim();

            if (description.length() > 2000) {
                throw new IllegalArgumentException(
                        "Description cannot exceed 2000 characters"
                );
            }

            if (description.isBlank()) {
                description = null;
            }
        }

        // 4. Validate due date
        LocalDate parsedDueDate = null;

        if (dueDate != null && !dueDate.isBlank()) {
            try {
                parsedDueDate = LocalDate.parse(
                        dueDate.trim()
                );
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException(
                        "Due date must use YYYY-MM-DD format"
                );
            }

            if (parsedDueDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Due date cannot be in the past"
                );
            }
        }

        // 5. Validate priority
        TaskPriority parsedPriority = TaskPriority.MEDIUM;

        if (priority != null && !priority.isBlank()) {
            try {
                parsedPriority = TaskPriority.valueOf(
                        priority.trim().toUpperCase(
                                java.util.Locale.ROOT
                        )
                );
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "Priority must be LOW, MEDIUM, or HIGH"
                );
            }
        }

        // 6. Find a farm owned by the authenticated user
        Farm farm;

        if (requestedFarmId != null) {
            if (requestedFarmId <= 0) {
                throw new IllegalArgumentException(
                        "Farm ID must be a positive integer"
                );
            }

            farm = farmRepository
                    .findByIdAndUserId(
                            requestedFarmId,
                            authenticatedUserId
                    )
                    .orElseThrow(() ->
                            new FarmNotFoundException(
                                    "Farm not found or access denied"
                            )
                    );

        } else {
            farm = farmRepository
                    .findFirstByUserIdOrderByIdAsc(
                            authenticatedUserId
                    )
                    .orElseThrow(() ->
                            new FarmNotFoundException(
                                    "No registered farm was found"
                            )
                    );
        }

        // 7. Create a task that requires approval
        FarmTask task = new FarmTask(
                authenticatedUserId,
                farm.getId(),
                title,
                description,
                parsedDueDate,
                parsedPriority
        );

        return farmTaskRepository.save(task);
    }

    @Transactional
    public FarmTask approveTask(
            Long authenticatedUserId,
            Long taskId
    ) {
        FarmTask task = farmTaskRepository
                .findByIdAndUserId(
                        taskId,
                        authenticatedUserId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found or access denied"
                        )
                );

        task.approve();

        return task;
    }

    @Transactional
    public FarmTask cancelTask(
            Long authenticatedUserId,
            Long taskId
    ) {
        FarmTask task = farmTaskRepository
                .findByIdAndUserId(
                        taskId,
                        authenticatedUserId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found or access denied"
                        )
                );

        task.cancel();

        return task;
    }
}