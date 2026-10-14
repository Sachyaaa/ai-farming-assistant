package com.sachin.ai_farming_assistant.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FarmToolConfig {

    public Map<String, Object> getFarmDetailsTool() {
        return Map.of(
                "functionDeclarations",
                List.of(getFarmDetailsDeclaration())
        );
    }

    public Map<String, Object> getCreateFarmTaskTool() {
        return Map.of(
                "functionDeclarations",
                List.of(getCreateFarmTaskDeclaration())
        );
    }

    public Map<String, Object> getAllTools() {
        return Map.of(
                "functionDeclarations",
                List.of(
                        getFarmDetailsDeclaration(),
                        getCreateFarmTaskDeclaration()
                )
        );
    }

    private Map<String, Object> getFarmDetailsDeclaration() {
        return Map.of(
                "name", "getFarmDetails",
                "description", """
                        Retrieves farm details for the currently authenticated user.
                        If farmId is supplied, it must belong to that user.
                        If omitted, retrieves the user's default farm.
                        """,
                "parameters", Map.of(
                        "type", "OBJECT",
                        "properties", Map.of(
                                "farmId", Map.of(
                                        "type", "INTEGER",
                                        "description", "Optional farm ID"
                                )
                        )
                )
        );
    }

    private Map<String, Object> getCreateFarmTaskDeclaration() {
        return Map.of(
                "name", "createFarmTask",
                "description", """
                        Creates a farm task for the authenticated user.
                        Use this tool only when the user explicitly requests
                        task creation. Do not create tasks when the user is
                        only asking for advice or recommendations.
                        The task will remain PENDING_APPROVAL until the
                        user explicitly approves it.
                        Resolve relative due dates using the current date
                        provided in the system instruction.
                        Return the exact declared function name and argument
                        names when requesting this tool.
                        """,
                "parameters", Map.of(
                        "type", "OBJECT",
                        "properties", Map.of(
                                "farmId", Map.of(
                                        "type", "INTEGER",
                                        "description", "Optional ID of the user's farm"
                                ),
                                "title", Map.of(
                                        "type", "STRING",
                                        "description", "Short title for the task"
                                ),
                                "description", Map.of(
                                        "type", "STRING",
                                        "description", "Optional task details"
                                ),
                                "dueDate", Map.of(
                                        "type", "STRING",
                                        "description", "Optional due date in YYYY-MM-DD format"
                                ),
                                "priority", Map.of(
                                        "type", "STRING",
                                        "enum", List.of("LOW", "MEDIUM", "HIGH"),
                                        "description", "Task priority"
                                )
                        ),
                        "required", List.of("title")
                )
        );
    }
}