package com.sachin.ai_farming_assistant;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Service
public class AwsSecretsManagerService {

    private final SecretsManagerClient secretsManagerClient;

    public AwsSecretsManagerService() {
        this.secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.AP_SOUTHEAST_2)
                .build();
    }

    public String getSecret(String secretName) {

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response =
                secretsManagerClient.getSecretValue(request);

        return response.secretString();
    }
}