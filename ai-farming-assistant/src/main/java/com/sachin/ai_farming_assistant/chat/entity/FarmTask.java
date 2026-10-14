package com.sachin.ai_farming_assistant.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "farm_tasks",
        indexes = {
                @Index(
                        name = "idx_farm_tasks_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_farm_tasks_farm_id",
                        columnList = "farm_id"
                )
        }
)
public class FarmTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "farm_id", nullable = false)
    private Long farmId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FarmTaskStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected FarmTask() {
    }

    public FarmTask(
            Long userId,
            Long farmId,
            String title,
            String description,
            LocalDate dueDate,
            TaskPriority priority
    ) {
        this.userId = userId;
        this.farmId = farmId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = FarmTaskStatus.PENDING_APPROVAL;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getFarmId() {
        return farmId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public FarmTaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void approve() {
        if (this.status != FarmTaskStatus.PENDING_APPROVAL) {
            throw new IllegalStateException(
                    "Only pending tasks can be approved"
            );
        }

        this.status = FarmTaskStatus.OPEN;
    }

    public void cancel() {
        if (this.status != FarmTaskStatus.PENDING_APPROVAL) {
            throw new IllegalStateException(
                    "Only pending tasks can be cancelled"
            );
        }

        this.status = FarmTaskStatus.CANCELLED;
    }
}