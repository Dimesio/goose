package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for APIUtil demonstrating string formatting and text block coverage.
 * Uses modern Java features including text blocks (Java 15), 
 * advanced string formatting capabilities, and pattern matching.
 */
public class APIUtilTest {

    // Mock APIUtil class demonstrating string formatting and text blocks
    public static class APIUtil {
        
        // String formatting examples
        public static String createApiEndpoint(String baseUrl, String resource, String id) {
            // Using String.format for URL construction
            return String.format("%s/api/v1/%s/%s", baseUrl, resource, id);
        }

        public static String formatErrorMessage(String operation, int statusCode, String errorDetails) {
            // String formatting for error messages
            return String.format("API Error during %s: HTTP %d - %s", operation, statusCode, errorDetails);
        }

        public static String generateAuthHeader(String tokenType, String token) {
            // String formatting for authorization header
            return String.format("%s %s", tokenType, token);
        }

        // Text blocks for complex API responses and requests
        public static String createJsonPayload(String name, String email, int age, boolean active) {
            // Using text block for JSON structure with String.format
            String template = """
                {
                    "user": {
                        "name": "%s",
                        "email": "%s",
                        "age": %d,
                        "active": %s,
                        "created_at": "%s",
                        "metadata": {
                            "source": "api",
                            "version": "1.0"
                        }
                    }
                }
                """;
            return String.format(template, name, email, age, active, java.time.Instant.now());
        }

        public static String generateSqlQuery(String tableName, String whereClause, int limit) {
            // Text block with string formatting for SQL
            String template = """
                SELECT 
                    id,
                    name,
                    email,
                    created_at,
                    updated_at
                FROM %s
                WHERE %s
                ORDER BY created_at DESC
                LIMIT %d;
                """;
            return String.format(template, tableName, whereClause, limit);
        }

        public static String createApiDocumentation(String endpoint, String method, String description) {
            // Complex text block for API documentation
            String template = """
                ## API Endpoint: %s
                
                **Method:** %s
                **Description:** %s
                
                ### Request Headers
                ```
                Content-Type: application/json
                Authorization: Bearer <token>
                X-API-Version: 1.0
                ```
                
                ### Example Request
                ```bash
                curl -X %s %s \\
                  -H "Content-Type: application/json" \\
                  -H "Authorization: Bearer your-token-here"
                ```
                
                ### Response Codes
                - 200: Success
                - 400: Bad Request
                - 401: Unauthorized
                - 404: Not Found
                - 500: Internal Server Error
                """;
            return String.format(template, endpoint, method, description, method, endpoint);
        }

        // Advanced string formatting with processors
        public static String createLogEntry(String level, String component, String message, Object... args) {
            var timestamp = java.time.LocalDateTime.now();
            var formattedArgs = args.length > 0 ? String.format(" - Args: %s", java.util.Arrays.toString(args)) : "";
            
            return String.format("[%s] %s: %s - %s%s", timestamp, level.toUpperCase(), component, message, formattedArgs);
        }

        // Text block with embedded expressions
        public static String generateConfigFile(String appName, String environment, int port, boolean debug) {
            String template = """
                # Configuration for %s
                # Environment: %s
                # Generated: %s
                
                app:
                  name: "%s"
                  environment: "%s"
                  version: "1.0.0"
                  
                server:
                  port: %d
                  debug: %s
                  max-connections: %d
                  timeout: "%s"
                  
                database:
                  url: "jdbc:postgresql://localhost:5432/%s_%s"
                  username: "%s_user"
                  pool-size: %d
                  
                logging:
                  level: %s
                  pattern: "%%d{yyyy-MM-dd HH:mm:ss} [%s] %%level %%logger - %%msg%%n"
                """;
            
            return String.format(template,
                appName, environment, java.time.LocalDateTime.now(),
                appName, environment,
                port, debug,
                debug ? 10 : 100,
                debug ? "30s" : "10s",
                appName.toLowerCase(), environment,
                appName.toLowerCase(),
                environment.equals("production") ? 20 : 5,
                debug ? "DEBUG" : "INFO",
                environment
            );
        }

        // Multi-line string for HTML generation
        public static String generateHtmlReport(String title, java.util.List<String> items, String cssClass) {
            var itemsList = items.stream()
                .map(item -> String.format("        <li>%s</li>", item))
                .reduce("", (acc, item) -> acc + item + "\n");
                
            String template = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        .%s {
                            color: #333;
                            font-family: Arial, sans-serif;
                            margin: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="%s">
                        <h1>%s</h1>
                        <p>Generated on: %s</p>
                        <ul>
                %s        </ul>
                    </div>
                </body>
                </html>
                """;
                
            return String.format(template, title, cssClass, cssClass, title, java.time.LocalDateTime.now(), itemsList);
        }

        // String formatting with conditional logic
        public static String createApiResponse(String status, Object data, String errorMessage) {
            var isError = !"success".equals(status);
            
            String template = """
                {
                    "status": "%s",
                    "timestamp": "%s",
                    %s
                }
                """;
                
            String dataOrError = isError ? 
                String.format("\"error\": \"%s\"", errorMessage) : 
                String.format("\"data\": %s", data instanceof String ? String.format("\"%s\"", data) : data);
                
            return String.format(template, status, java.time.Instant.now(), dataOrError);
        }

        // Text block for complex regex patterns
        public static String getEmailValidationPattern() {
            return """
                ^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?
                (?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$
                """.strip();
        }

        // Text block for shell scripts
        public static String generateDeploymentScript(String appName, String version, String environment) {
            String template = """
                #!/bin/bash
                
                # Deployment script for %s
                # Version: %s
                # Environment: %s
                # Generated: %s
                
                set -e
                
                APP_NAME="%s"
                VERSION="%s"
                ENVIRONMENT="%s"
                DOCKER_IMAGE="%s:%s"
                
                echo "Starting deployment of $APP_NAME version $VERSION to $ENVIRONMENT"
                
                # Pull the latest image
                docker pull $DOCKER_IMAGE
                
                # Stop existing container
                docker stop $APP_NAME-$ENVIRONMENT || true
                docker rm $APP_NAME-$ENVIRONMENT || true
                
                # Start new container
                docker run -d \\
                  --name $APP_NAME-$ENVIRONMENT \\
                  --restart unless-stopped \\
                  -p %s:8080 \\
                  -e ENVIRONMENT=$ENVIRONMENT \\
                  $DOCKER_IMAGE
                
                echo "Deployment completed successfully"
                """;
                
            return String.format(template,
                appName, version, environment, java.time.LocalDateTime.now(),
                appName, version, environment, appName, version,
                environment.equals("production") ? "80" : "8080"
            );
        }
    }

    private APIUtil apiUtil;

    @BeforeEach
    void setUp() {
        apiUtil = new APIUtil();
    }

    @Test
    @DisplayName("Should create API endpoints using string formatting")
    void testApiEndpointCreation() {
        String endpoint = APIUtil.createApiEndpoint("https://api.example.com", "users", "123");
        assertEquals("https://api.example.com/api/v1/users/123", endpoint);
        
        String resourceEndpoint = APIUtil.createApiEndpoint("http://localhost:8080", "products", "456");
        assertEquals("http://localhost:8080/api/v1/products/456", resourceEndpoint);
    }

    @Test
    @DisplayName("Should format error messages using string formatting")
    void testErrorMessageFormatting() {
        String errorMsg = APIUtil.formatErrorMessage("GET /users", 404, "User not found");
        assertEquals("API Error during GET /users: HTTP 404 - User not found", errorMsg);
        
        String serverError = APIUtil.formatErrorMessage("POST /orders", 500, "Internal server error");
        assertEquals("API Error during POST /orders: HTTP 500 - Internal server error", serverError);
    }

    @Test
    @DisplayName("Should generate authorization headers using string formatting")
    void testAuthHeaderGeneration() {
        String bearerHeader = APIUtil.generateAuthHeader("Bearer", "abc123token");
        assertEquals("Bearer abc123token", bearerHeader);
        
        String basicHeader = APIUtil.generateAuthHeader("Basic", "dXNlcjpwYXNz");
        assertEquals("Basic dXNlcjpwYXNz", basicHeader);
    }

    @Test
    @DisplayName("Should create JSON payload using text blocks and string formatting")
    void testJsonPayloadCreation() {
        String json = APIUtil.createJsonPayload("John Doe", "john@example.com", 30, true);
        
        assertNotNull(json);
        assertTrue(json.contains("\"name\": \"John Doe\""));
        assertTrue(json.contains("\"email\": \"john@example.com\""));
        assertTrue(json.contains("\"age\": 30"));
        assertTrue(json.contains("\"active\": true"));
        assertTrue(json.contains("\"source\": \"api\""));
        assertTrue(json.contains("\"version\": \"1.0\""));
        assertTrue(json.contains("\"created_at\":"));
    }

    @Test
    @DisplayName("Should generate SQL queries using text blocks")
    void testSqlQueryGeneration() {
        String sql = APIUtil.generateSqlQuery("users", "status = 'active'", 10);
        
        assertTrue(sql.contains("SELECT"));
        assertTrue(sql.contains("FROM users"));
        assertTrue(sql.contains("WHERE status = 'active'"));
        assertTrue(sql.contains("LIMIT 10"));
        assertTrue(sql.contains("ORDER BY created_at DESC"));
    }

    @Test
    @DisplayName("Should create API documentation using text blocks")
    void testApiDocumentationGeneration() {
        String docs = APIUtil.createApiDocumentation("/api/v1/users", "GET", "Retrieve user list");
        
        assertTrue(docs.contains("## API Endpoint: /api/v1/users"));
        assertTrue(docs.contains("**Method:** GET"));
        assertTrue(docs.contains("**Description:** Retrieve user list"));
        assertTrue(docs.contains("Content-Type: application/json"));
        assertTrue(docs.contains("curl -X GET /api/v1/users"));
        assertTrue(docs.contains("200: Success"));
        assertTrue(docs.contains("404: Not Found"));
    }

    @Test
    @DisplayName("Should create structured log entries")
    void testLogEntryCreation() {
        String logEntry = APIUtil.createLogEntry("INFO", "UserService", "User created successfully");
        
        assertTrue(logEntry.contains("INFO: UserService - User created successfully"));
        assertTrue(logEntry.matches(".*\\[\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*\\].*"));
        
        String logWithArgs = APIUtil.createLogEntry("ERROR", "DatabaseService", "Connection failed", "timeout", 5000);
        assertTrue(logWithArgs.contains("Args: [timeout, 5000]"));
    }

    @Test
    @DisplayName("Should generate configuration files using text blocks")
    void testConfigFileGeneration() {
        String config = APIUtil.generateConfigFile("MyApp", "development", 8080, true);
        
        assertTrue(config.contains("name: \"MyApp\""));
        assertTrue(config.contains("environment: \"development\""));
        assertTrue(config.contains("port: 8080"));
        assertTrue(config.contains("debug: true"));
        assertTrue(config.contains("max-connections: 10")); // debug mode
        assertTrue(config.contains("timeout: \"30s\"")); // debug mode
        assertTrue(config.contains("level: DEBUG")); // debug mode
        assertTrue(config.contains("myapp_development")); // lowercase database name
        
        String prodConfig = APIUtil.generateConfigFile("ProdApp", "production", 80, false);
        assertTrue(prodConfig.contains("max-connections: 100")); // production mode
        assertTrue(prodConfig.contains("pool-size: 20")); // production database pool
        assertTrue(prodConfig.contains("level: INFO")); // production logging
    }

    @Test
    @DisplayName("Should generate HTML reports using string formatting")
    void testHtmlReportGeneration() {
        java.util.List<String> items = java.util.List.of("Item 1", "Item 2", "Item 3");
        String html = APIUtil.generateHtmlReport("Test Report", items, "report-style");
        
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<title>Test Report</title>"));
        assertTrue(html.contains("class=\"report-style\""));
        assertTrue(html.contains("<h1>Test Report</h1>"));
        assertTrue(html.contains("<li>Item 1</li>"));
        assertTrue(html.contains("<li>Item 2</li>"));
        assertTrue(html.contains("<li>Item 3</li>"));
        assertTrue(html.contains("Generated on:"));
    }

    @Test
    @DisplayName("Should create API responses with conditional logic")
    void testApiResponseCreation() {
        String successResponse = APIUtil.createApiResponse("success", "User data", null);
        assertTrue(successResponse.contains("\"status\": \"success\""));
        assertTrue(successResponse.contains("\"data\": \"User data\""));
        assertFalse(successResponse.contains("error"));
        
        String errorResponse = APIUtil.createApiResponse("error", null, "Validation failed");
        assertTrue(errorResponse.contains("\"status\": \"error\""));
        assertTrue(errorResponse.contains("\"error\": \"Validation failed\""));
        assertFalse(errorResponse.contains("data"));
    }

    @Test
    @DisplayName("Should provide email validation pattern using text blocks")
    void testEmailValidationPattern() {
        String pattern = APIUtil.getEmailValidationPattern();
        
        assertNotNull(pattern);
        // strip() removes leading/trailing whitespace but may not remove internal newlines
        // assertTrue(pattern.contains("@")); // Just check it has email-like content
        assertTrue(pattern.startsWith("^"));
        assertTrue(pattern.endsWith("$"));
        assertTrue(pattern.contains("@"));
    }

    @Test
    @DisplayName("Should generate deployment scripts using text blocks")
    void testDeploymentScriptGeneration() {
        String script = APIUtil.generateDeploymentScript("MyApp", "1.2.3", "production");
        
        assertTrue(script.contains("#!/bin/bash"));
        assertTrue(script.contains("APP_NAME=\"MyApp\""));
        assertTrue(script.contains("VERSION=\"1.2.3\""));
        assertTrue(script.contains("ENVIRONMENT=\"production\""));
        assertTrue(script.contains("DOCKER_IMAGE=\"MyApp:1.2.3\""));
        assertTrue(script.contains("-p 80:8080")); // production port mapping
        assertTrue(script.contains("docker pull"));
        assertTrue(script.contains("docker stop"));
        assertTrue(script.contains("docker run"));
        
        String devScript = APIUtil.generateDeploymentScript("DevApp", "1.0.0", "development");
        assertTrue(devScript.contains("-p 8080:8080")); // development port mapping
    }

    @Test
    @DisplayName("Should handle special characters in string formatting")
    void testSpecialCharacterHandling() {
        String endpoint = APIUtil.createApiEndpoint("https://api.example.com", "users-profiles", "test@123");
        assertEquals("https://api.example.com/api/v1/users-profiles/test@123", endpoint);
        
        String errorMsg = APIUtil.formatErrorMessage("GET /api", 400, "Invalid parameter: 'filter=name>=\"John\"'");
        assertTrue(errorMsg.contains("Invalid parameter: 'filter=name>=\"John\"'"));
    }

    @Test
    @DisplayName("Should demonstrate text block indentation handling")
    void testTextBlockIndentation() {
        String json = APIUtil.createJsonPayload("Test User", "test@example.com", 25, false);
        
        // Text blocks should maintain proper indentation
        String[] lines = json.split("\n");
        assertTrue(lines.length > 1);
        
        // Check that nested objects are properly indented
        boolean hasIndentation = false;
        for (String line : lines) {
            if (line.startsWith("    ") && line.trim().length() > 0) {
                hasIndentation = true;
                break;
            }
        }
        assertTrue(hasIndentation, "Text block should maintain indentation");
    }

    @Test
    @DisplayName("Should handle null and empty values in string templates")
    void testNullAndEmptyValueHandling() {
        String endpoint = APIUtil.createApiEndpoint("https://api.example.com", "", "123");
        assertEquals("https://api.example.com/api/v1//123", endpoint);
        
        String logEntry = APIUtil.createLogEntry("INFO", "TestComponent", "");
        assertTrue(logEntry.contains("INFO: TestComponent - "));
    }

    @Test
    @DisplayName("Should demonstrate multi-line string formatting expressions")
    void testMultiLineStringFormattingExpressions() {
        String configWithComplexLogic = APIUtil.generateConfigFile("ComplexApp", "staging", 3000, false);
        
        // Verify conditional expressions work correctly
        assertTrue(configWithComplexLogic.contains("max-connections: 100")); // not debug
        assertTrue(configWithComplexLogic.contains("pool-size: 5")); // not production
        assertTrue(configWithComplexLogic.contains("level: INFO")); // not debug
        assertTrue(configWithComplexLogic.contains("complexapp_staging")); // lowercase conversion
    }
}