package com.sachin.ai_farming_assistant.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "farms",
        indexes = {
                @Index(
                        name = "idx_farms_user_id",
                        columnList = "user_id"
                )
        }
)
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "farm_name", nullable = false)
    private String farmName;

    @Column(nullable = false)
    private String crop;

    @Column(name = "farm_size_acres", nullable = false)
    private double farmSizeAcres;

    @Column(name = "soil_type")
    private String soilType;

    @Column(name = "irrigation_method")
    private String irrigationMethod;

    private String location;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Farm() {
    }

    public Farm(
            Long userId,
            String farmName,
            String crop,
            double farmSizeAcres,
            String soilType,
            String irrigationMethod,
            String location
    ) {
        this.userId = userId;
        this.farmName = farmName;
        this.crop = crop;
        this.farmSizeAcres = farmSizeAcres;
        this.soilType = soilType;
        this.irrigationMethod = irrigationMethod;
        this.location = location;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFarmName() {
        return farmName;
    }

    public String getCrop() {
        return crop;
    }

    public double getFarmSizeAcres() {
        return farmSizeAcres;
    }

    public String getSoilType() {
        return soilType;
    }

    public String getIrrigationMethod() {
        return irrigationMethod;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}