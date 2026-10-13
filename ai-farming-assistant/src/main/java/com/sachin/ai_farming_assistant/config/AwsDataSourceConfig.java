//package com.sachin.ai_farming_assistant.config;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sachin.ai_farming_assistant.AwsSecretsManagerService;
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class AwsDataSourceConfig {
//
//    private final AwsSecretsManagerService secretsManagerService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public AwsDataSourceConfig(
//            AwsSecretsManagerService secretsManagerService) {
//        this.secretsManagerService = secretsManagerService;
//    }
//
//    @Bean
//    public DataSource dataSource(
//            @Value("${DB_SSL_ROOT_CERT}") String sslRootCert) throws Exception {
//
//        String secretJson = secretsManagerService.getSecret(
//                "farmassist/dev/database");
//
//        JsonNode secret = objectMapper.readTree(secretJson);
//
//        String host = secret.get("host").asText();
//        int port = secret.get("port").asInt();
//        String database = secret.get("dbname").asText();
//        String username = secret.get("username").asText();
//        String password = secret.get("password").asText();
//
//        String jdbcUrl = String.format(
//                "jdbc:postgresql://%s:%d/%s?sslmode=verify-full&sslrootcert=%s",
//                host,
//                port,
//                database,
//                sslRootCert
//        );
//
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(jdbcUrl);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        dataSource.setConnectionTimeout(10000);
//
//        return dataSource;
//    }
//}