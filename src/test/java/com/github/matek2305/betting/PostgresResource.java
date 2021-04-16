package com.github.matek2305.betting;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    static PostgreSQLContainer<?> db =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("betting_test_db")
                    .withUsername("betting")
                    .withPassword("betting");

    @Override
    public Map<String, String> start() {
        db.start();
        return Map.ofEntries(
                Map.entry("quarkus.datasource.jdbc.url", db.getJdbcUrl()),
                Map.entry("quarkus.datasource.username", db.getUsername()),
                Map.entry("quarkus.datasource.password", db.getPassword())
        );
    }

    @Override
    public void stop() {
        db.stop();
    }
}
