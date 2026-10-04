package com.sachin.ai_farming_assistant.chat.dto;

public record CropQuery(

        QueryCategory category,

        String crop,

        Urgency urgency,

        boolean needsMoreInformation

) {

}
