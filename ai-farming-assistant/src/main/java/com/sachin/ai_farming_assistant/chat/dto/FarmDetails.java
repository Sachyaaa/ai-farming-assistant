package com.sachin.ai_farming_assistant.chat.dto;

public record FarmDetails(
        Long farmId,
        String farmName,
        String crop,
        double farmSizeAcres,
        String soilType,
        String irrigationMethod,
        String location
) {
}