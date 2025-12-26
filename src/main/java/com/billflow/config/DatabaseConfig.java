package com.billflow.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@EnableRetry
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        logger.info("Configuring HikariCP DataSource");
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl(properties.getUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setDriverClassName(properties.getDriverClassName());
        
        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(20000); // 20 seconds
        config.setValidationTimeout(5000); // 5 seconds
        config.setLeakDetectionThreshold(60000); // 1 minute
        config.setConnectionTestQuery("SELECT 1");
        
        // MySQL specific settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useSSL", "true");
        config.addDataSourceProperty("requireSSL", "true");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        
        HikariDataSource dataSource = new HikariDataSource(config);
        
        // Test connection on startup
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Database connection established successfully");
        } catch (SQLException e) {
            logger.error("Failed to establish database connection: {}", e.getMessage(), e);
            throw new RuntimeException("Database connection failed", e);
        }
        
        return dataSource;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        
        // Configure retry policies for database operations
        return template;
    }
}

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
class DatabaseConnectionChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

    @Retryable(
        value = {SQLException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void checkConnection(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                logger.info("Database connection is valid");
            }
        }
    }
}