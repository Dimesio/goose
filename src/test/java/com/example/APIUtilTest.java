package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Tests for APIUtil demonstrating text blocks and formatted strings
 * This simulates API utility functionality that might be used in the goose project
 */
public class APIUtilTest {

    @Test
    @DisplayName("Should use formatted strings for dynamic content generation")
    void shouldUseFormattedStringsForDynamicContentGeneration() {
        // Given
        String apiKey = "abc123";
        String endpoint = "https://api.example.com/v1/users";
        int userId = 12345;
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 14, 30);

        // When - using String.format for dynamic content generation
        String requestUrl = String.format("%s/%d?api_key=%s&timestamp=%s", endpoint, userId, apiKey, timestamp);
        String logMessage = String.format("API Request to %s for user %d at %s", endpoint, userId, timestamp);
        String authHeader = String.format("Bearer %s", apiKey);

        // Then
        assertThat(requestUrl).isEqualTo("https://api.example.com/v1/users/12345?api_key=abc123&timestamp=2024-01-15T14:30");
        assertThat(logMessage).contains("API Request to https://api.example.com/v1/users for user 12345");
        assertThat(authHeader).isEqualTo("Bearer abc123");
    }

    @Test
    @DisplayName("Should use text blocks for complex API payloads")
    void shouldUseTextBlocksForComplexApiPayloads() {
        // Given
        String userId = "user123";
        String userName = "John Doe";
        String email = "john.doe@example.com";
        String role = "admin";

        // When - using text blocks for JSON payload with String.format
        String jsonPayload = String.format("""
            {
              "user": {
                "id": "%s",
                "name": "%s",
                "email": "%s",
                "role": "%s",
                "preferences": {
                  "notifications": true,
                  "theme": "dark"
                },
                "metadata": {
                  "created_at": "%s",
                  "version": "1.0"
                }
              }
            }
            """, userId, userName, email, role, LocalDateTime.now());

        // Then
        assertThat(jsonPayload)
            .contains("\"id\": \"user123\"")
            .contains("\"name\": \"John Doe\"")
            .contains("\"email\": \"john.doe@example.com\"")
            .contains("\"role\": \"admin\"")
            .contains("\"notifications\": true")
            .contains("\"theme\": \"dark\"");
    }

    @Test
    @DisplayName("Should generate SQL queries using text blocks and formatted strings")
    void shouldGenerateSqlQueriesUsingTextBlocksAndFormattedStrings() {
        // Given
        String tableName = "users";
        String userId = "123";
        String status = "active";
        int limit = 10;

        // When - using text blocks for complex SQL with String.format
        String complexQuery = String.format("""
            SELECT 
                u.id,
                u.name,
                u.email,
                u.created_at,
                p.preferences_json
            FROM %s u
            LEFT JOIN user_preferences p ON u.id = p.user_id
            WHERE u.status = '%s'
                AND u.id > %s
            ORDER BY u.created_at DESC
            LIMIT %d
            """, tableName, status, userId, limit);

        // Then
        assertThat(complexQuery)
            .contains("FROM users u")
            .contains("WHERE u.status = 'active'")
            .contains("AND u.id > 123")
            .contains("LIMIT 10");
    }

    @Test
    @DisplayName("Should handle error messages with formatted strings")
    void shouldHandleErrorMessagesWithFormattedStrings() {
        // Given
        String operation = "CREATE_USER";
        String errorCode = "VALIDATION_ERROR";
        String field = "email";
        String value = "invalid-email";
        int attemptCount = 3;

        // When - using String.format for error messages
        String errorMessage = String.format("Operation '%s' failed with error '%s'. Invalid value '%s' for field '%s'. Attempt %d/3.", 
                                           operation, errorCode, value, field, attemptCount);
        
        String detailedError = String.format("""
            Error Details:
            - Operation: %s
            - Error Code: %s
            - Field: %s
            - Invalid Value: %s
            - Attempt: %d/3
            - Timestamp: %s
            """, operation, errorCode, field, value, attemptCount, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Then
        assertThat(errorMessage).contains("Operation 'CREATE_USER' failed");
        assertThat(errorMessage).contains("Invalid value 'invalid-email' for field 'email'");
        assertThat(detailedError).contains("- Operation: CREATE_USER");
        assertThat(detailedError).contains("- Error Code: VALIDATION_ERROR");
    }

    @Test
    @DisplayName("Should format configuration files using text blocks")
    void shouldFormatConfigurationFilesUsingTextBlocks() {
        // Given
        String appName = "goose-api";
        String version = "1.0.31";
        String environment = "production";
        int port = 8080;
        String dbUrl = "postgresql://localhost:5432/goose";

        // When - using text blocks for configuration with String.format
        String yamlConfig = String.format("""
            application:
              name: %s
              version: %s
              environment: %s
            
            server:
              port: %d
              ssl:
                enabled: true
                key-store: /path/to/keystore
            
            database:
              url: %s
              driver: org.postgresql.Driver
              connection-pool:
                initial-size: 5
                max-size: 20
            
            logging:
              level:
                root: INFO
                com.example: DEBUG
            """, appName, version, environment, port, dbUrl);

        // Then
        assertThat(yamlConfig)
            .contains("name: goose-api")
            .contains("version: 1.0.31")
            .contains("environment: production")
            .contains("port: 8080")
            .contains("url: postgresql://localhost:5432/goose");
    }

    @Test
    @DisplayName("Should generate API documentation using text blocks")
    void shouldGenerateApiDocumentationUsingTextBlocks() {
        // Given
        String endpoint = "/api/v1/users";
        String method = "POST";
        String description = "Create a new user";

        // When - using text blocks for API documentation with String.format
        String apiDoc = String.format("""
            ## %s %s
            
            **Description:** %s
            
            ### Request Body
            ```json
            {
              "name": "string",
              "email": "string",
              "role": "admin|user|viewer"
            }
            ```
            
            ### Response
            ```json
            {
              "id": "string",
              "name": "string",
              "email": "string",
              "role": "string",
              "created_at": "ISO 8601 timestamp"
            }
            ```
            
            ### Error Responses
            - **400**: Bad Request - Invalid input data
            - **409**: Conflict - User already exists
            - **500**: Internal Server Error
            """, method, endpoint, description);

        // Then
        assertThat(apiDoc)
            .contains("## POST /api/v1/users")
            .contains("**Description:** Create a new user")
            .contains("### Request Body")
            .contains("### Response")
            .contains("### Error Responses");
    }

    @Test
    @DisplayName("Should handle HTML template generation with formatted strings")
    void shouldHandleHtmlTemplateGenerationWithFormattedStrings() {
        // Given
        String title = "Goose API Dashboard";
        String userName = "Admin User";
        int userCount = 1250;
        String lastUpdate = "2024-01-15 14:30:00";

        // When - using text blocks for HTML templates with String.format
        String htmlTemplate = String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { background-color: #f4f4f4; padding: 10px; }
                    .stats { display: flex; gap: 20px; margin: 20px 0; }
                    .stat-box { border: 1px solid #ddd; padding: 15px; border-radius: 5px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>%s</h1>
                    <p>Welcome, %s</p>
                </div>
                
                <div class="stats">
                    <div class="stat-box">
                        <h3>Total Users</h3>
                        <p>%d</p>
                    </div>
                    <div class="stat-box">
                        <h3>Last Update</h3>
                        <p>%s</p>
                    </div>
                </div>
            </body>
            </html>
            """, title, title, userName, userCount, lastUpdate);

        // Then
        assertThat(htmlTemplate)
            .contains("<title>Goose API Dashboard</title>")
            .contains("<h1>Goose API Dashboard</h1>")
            .contains("<p>Welcome, Admin User</p>")
            .contains("<p>1250</p>")
            .contains("<p>2024-01-15 14:30:00</p>");
    }

    @Test
    @DisplayName("Should escape special characters in formatted strings safely")
    void shouldEscapeSpecialCharactersInFormattedStringsSafely() {
        // Given
        String userInput = "O'Reilly & Associates";
        String description = "Company with \"quotes\" and <tags>";
        
        // When - handling special characters with String.format
        String escapedSql = String.format("SELECT * FROM companies WHERE name = '%s'", userInput.replace("'", "''"));
        String escapedHtml = String.format("<div title=\"%s\">Content</div>", 
                                          description.replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;"));
        
        // Then
        assertThat(escapedSql).contains("O''Reilly & Associates");
        assertThat(escapedHtml).contains("&quot;quotes&quot; and &lt;tags&gt;");
    }
}