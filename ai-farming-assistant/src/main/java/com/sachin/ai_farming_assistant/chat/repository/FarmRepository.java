package com.sachin.ai_farming_assistant.chat.repository;

import com.sachin.ai_farming_assistant.chat.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmRepository
        extends JpaRepository<Farm, Long> {

    Optional<Farm> findByIdAndUserId(
            Long farmId,
            Long userId
    );

    List<Farm> findAllByUserId(Long userId);

    Optional<Farm> findFirstByUserIdOrderByIdAsc(
            Long userId
    );
}