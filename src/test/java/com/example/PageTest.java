package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;
import java.util.Optional;

/**
 * Tests for Page class demonstrating pattern matching and Optional usage
 * This simulates a base page class that might be used in the goose application
 */
public class PageTest {

    private Page testPage;

    @BeforeEach
    void setUp() {
        testPage = new ConcretePage("test-page", "Test Page", true);
    }

    @Test
    @DisplayName("Should use pattern matching for instanceof checks")
    void shouldUsePatternMatchingForInstanceofChecks() {
        // Given
        Object pageObject = testPage;

        // When & Then - using pattern matching (Java 16+)
        if (pageObject instanceof ConcretePage cp) {
            assertThat(cp.getId()).isEqualTo("test-page");
            assertThat(cp.getTitle()).isEqualTo("Test Page");
            assertThat(cp.isVisible()).isTrue();
        } else {
            fail("Should be instance of ConcretePage");
        }
    }

    @Test
    @DisplayName("Should handle Optional navigation state safely")
    void shouldHandleOptionalNavigationStateSafely() {
        // Given
        Page pageWithNavigation = new ConcretePage("nav-page", "Navigation Page", true);
        Page pageWithoutNavigation = new ConcretePage("simple-page", "Simple Page", false);

        // When & Then - using Optional for null safety
        Optional<String> navStateWithNav = pageWithNavigation.getNavigationState();
        Optional<String> navStateWithoutNav = pageWithoutNavigation.getNavigationState();

        assertThat(navStateWithNav).isPresent();
        assertThat(navStateWithNav.get()).isEqualTo("NAVIGABLE");

        assertThat(navStateWithoutNav).isEmpty();
    }

    @Test
    @DisplayName("Should use pattern matching in switch expressions")
    void shouldUsePatternMatchingInSwitchExpressions() {
        // Given
        Page[] pages = {
            new ConcretePage("home", "Home", true),
            new AdminPage("admin", "Admin Panel", true, "ADMIN"),
            new ConcretePage("hidden", "Hidden Page", false)
        };

        // When & Then - using if-else instead of pattern matching for Java 17 compatibility
        for (Page page : pages) {
            String pageDescription;
            if (page instanceof AdminPage) {
                AdminPage ap = (AdminPage) page;
                pageDescription = String.format("Admin page: %s (Access: %s)", ap.getTitle(), ap.getAccessLevel());
            } else if (page instanceof ConcretePage) {
                ConcretePage cp = (ConcretePage) page;
                if (cp.isVisible()) {
                    pageDescription = String.format("Visible page: %s", cp.getTitle());
                } else {
                    pageDescription = String.format("Hidden page: %s", cp.getTitle());
                }
            } else {
                pageDescription = "Unknown page type";
            }

            switch (page.getId()) {
                case "home" -> assertThat(pageDescription).contains("Visible page: Home");
                case "admin" -> assertThat(pageDescription).contains("Admin page: Admin Panel (Access: ADMIN)");
                case "hidden" -> assertThat(pageDescription).contains("Hidden page: Hidden Page");
            }
        }
    }

    @Test
    @DisplayName("Should handle page validation with Optional chaining")
    void shouldHandlePageValidationWithOptionalChaining() {
        // Given
        Page validPage = new ConcretePage("valid", "Valid Page", true);
        Page invalidPage = new ConcretePage("", "Invalid Page", true);

        // When & Then - using Optional chaining for validation
        Optional<String> validPageId = validPage.getValidId();
        Optional<String> invalidPageId = invalidPage.getValidId();

        assertThat(validPageId).isPresent().get().isEqualTo("valid");
        assertThat(invalidPageId).isEmpty();

        // Chain operations safely
        String validResult = validPageId
            .map(id -> String.format("Page ID: %s", id))
            .orElse("No valid ID");

        String invalidResult = invalidPageId
            .map(id -> String.format("Page ID: %s", id))
            .orElse("No valid ID");

        assertThat(validResult).isEqualTo("Page ID: valid");
        assertThat(invalidResult).isEqualTo("No valid ID");
    }

    @Test
    @DisplayName("Should use sealed class pattern matching")
    void shouldUseSealedClassPatternMatching() {
        // Given
        PageState[] states = {
            new LoadingState(),
            new LoadedState("Content loaded successfully"),
            new ErrorState("Failed to load", 404)
        };

        // When & Then - using traditional if-else for Java 17 compatibility
        for (PageState state : states) {
            String stateDescription;
            if (state instanceof LoadingState) {
                stateDescription = "Page is loading...";
            } else if (state instanceof LoadedState) {
                LoadedState loaded = (LoadedState) state;
                stateDescription = String.format("Page loaded: %s", loaded.message());
            } else if (state instanceof ErrorState) {
                ErrorState error = (ErrorState) state;
                stateDescription = String.format("Error %d: %s", error.code(), error.message());
            } else {
                stateDescription = "Unknown state";
            }

            if (state instanceof LoadingState) {
                assertThat(stateDescription).isEqualTo("Page is loading...");
            } else if (state instanceof LoadedState) {
                assertThat(stateDescription).contains("Page loaded:");
            } else if (state instanceof ErrorState) {
                assertThat(stateDescription).contains("Error");
            }
        }
    }

    @Test
    @DisplayName("Should handle exception scenarios with pattern matching")
    void shouldHandleExceptionScenariosWithPatternMatching() {
        // Given
        Page problematicPage = new ConcretePage("problem", "Problem Page", true);

        // When & Then - exception handling with pattern matching
        try {
            problematicPage.performOperation("INVALID_OPERATION");
            fail("Should have thrown an exception");
        } catch (Exception e) {
            // Traditional exception handling for Java 17 compatibility
            String errorMessage;
            if (e instanceof IllegalArgumentException) {
                errorMessage = String.format("Invalid argument: %s", e.getMessage());
            } else if (e instanceof RuntimeException) {
                errorMessage = String.format("Runtime error: %s", e.getMessage());
            } else {
                errorMessage = String.format("Unexpected error: %s", e.getMessage());
            }

            assertThat(errorMessage).contains("Invalid argument:");
        }
    }

    // Supporting classes for testing - using non-sealed classes for Java 17 compatibility

    public abstract class Page {
        protected final String id;
        protected final String title;
        protected final boolean visible;

        public Page(String id, String title, boolean visible) {
            this.id = id;
            this.title = title;
            this.visible = visible;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public boolean isVisible() { return visible; }

        public Optional<String> getNavigationState() {
            return visible ? Optional.of("NAVIGABLE") : Optional.empty();
        }

        public Optional<String> getValidId() {
            return (id != null && !id.trim().isEmpty()) ? Optional.of(id) : Optional.empty();
        }

        public abstract void performOperation(String operation);
    }

    public final class ConcretePage extends Page {
        public ConcretePage(String id, String title, boolean visible) {
            super(id, title, visible);
        }

        @Override
        public void performOperation(String operation) {
            if ("INVALID_OPERATION".equals(operation)) {
                throw new IllegalArgumentException("Operation not supported: " + operation);
            }
        }
    }

    public final class AdminPage extends Page {
        private final String accessLevel;

        public AdminPage(String id, String title, boolean visible, String accessLevel) {
            super(id, title, visible);
            this.accessLevel = accessLevel;
        }

        public String getAccessLevel() { return accessLevel; }

        @Override
        public void performOperation(String operation) {
            if ("ADMIN_OPERATION".equals(operation)) {
                // Admin operation logic
            } else {
                throw new IllegalArgumentException("Unauthorized operation: " + operation);
            }
        }
    }

    // Interface for page states - using regular interface for Java 17 compatibility
    public interface PageState {}

    public static class LoadingState implements PageState {}
    public static class LoadedState implements PageState {
        private final String message;
        public LoadedState(String message) { this.message = message; }
        public String message() { return message; }
    }
    public static class ErrorState implements PageState {
        private final String message;
        private final int code;
        public ErrorState(String message, int code) { 
            this.message = message; 
            this.code = code; 
        }
        public String message() { return message; }
        public int code() { return code; }
    }
}