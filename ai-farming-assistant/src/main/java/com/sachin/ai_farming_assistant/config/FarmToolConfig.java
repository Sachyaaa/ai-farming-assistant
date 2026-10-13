package com.sachin.ai_farming_assistant.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FarmToolConfig {

    public Map<String, Object> getFarmDetailsTool() {

        Map<String, Object> functionDeclaration = Map.of(
                "name", "getFarmDetails",

                "description",
                """
                Retrieves farm details for the currently authenticated
                user. If a farmId is supplied, retrieves that farm only
                if it belongs to the authenticated user. If farmId is
                omitted, retrieves the user's default farm.
                Do not invent farm IDs or customer IDs.
                """,

                "parameters", Map.of(
                        "type", "OBJECT",

                        "properties", Map.of(
                                "farmId", Map.of(
                                        "type", "INTEGER",
                                        "description",
                                        "Optional ID of a farm belonging "
                                                + "to the authenticated user."
                                )
                        )
                )
        );

        return Map.of(
                "functionDeclarations",
                List.of(functionDeclaration)
        );
    }
}