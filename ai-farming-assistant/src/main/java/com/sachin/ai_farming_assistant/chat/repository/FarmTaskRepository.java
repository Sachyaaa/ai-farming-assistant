package com.sachin.ai_farming_assistant.chat.repository;

import com.sachin.ai_farming_assistant.chat.entity.FarmTask;
import com.sachin.ai_farming_assistant.chat.entity.FarmTaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmTaskRepository
        extends JpaRepository<FarmTask, Long> {

    Optional<FarmTask> findByIdAndUserId(
            Long taskId,
            Long userId
    );

    List<FarmTask> findAllByUserIdAndStatus(
            Long userId,
            FarmTaskStatus status
    );
}