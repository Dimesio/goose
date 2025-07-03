package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Test class for Page demonstrating pattern matching and Optional usage.
 * Uses modern Java features including sealed classes, pattern matching for instanceof,
 * switch expressions, and Optional handling.
 */
public class PageTest {

    // Sealed interface for page types
    public sealed interface PageContent 
        permits TextContent, ImageContent, VideoContent, EmptyContent {
    }

    public record TextContent(String text, int wordCount) implements PageContent {
        public TextContent {
            Objects.requireNonNull(text, "Text cannot be null");
            if (wordCount < 0) {
                throw new IllegalArgumentException("Word count cannot be negative");
            }
        }
    }

    public record ImageContent(String imageUrl, String altText, int width, int height) implements PageContent {
        public ImageContent {
            Objects.requireNonNull(imageUrl, "Image URL cannot be null");
            Objects.requireNonNull(altText, "Alt text cannot be null");
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Width and height must be positive");
            }
        }
    }

    public record VideoContent(String videoUrl, int durationSeconds, boolean hasSubtitles) implements PageContent {
        public VideoContent {
            Objects.requireNonNull(videoUrl, "Video URL cannot be null");
            if (durationSeconds <= 0) {
                throw new IllegalArgumentException("Duration must be positive");
            }
        }
    }

    public record EmptyContent() implements PageContent {}

    // Page class demonstrating pattern matching and Optional usage
    public static class Page {
        private final String title;
        private final Optional<PageContent> content;
        private final Optional<String> author;
        private final boolean isPublished;
        private final int version;

        public Page(String title, PageContent content, String author, boolean isPublished, int version) {
            this.title = Objects.requireNonNull(title, "Title cannot be null");
            this.content = Optional.ofNullable(content);
            this.author = Optional.ofNullable(author);
            this.isPublished = isPublished;
            this.version = version;
        }

        public String getTitle() {
            return title;
        }

        public Optional<PageContent> getContent() {
            return content;
        }

        public Optional<String> getAuthor() {
            return author;
        }

        public boolean isPublished() {
            return isPublished;
        }

        public int getVersion() {
            return version;
        }

        // Pattern matching for instanceof (Java 16+)
        public String getContentType() {
            return content.map(c -> {
                if (c instanceof TextContent text) {
                    return "Text content with " + text.wordCount() + " words";
                } else if (c instanceof ImageContent image) {
                    return "Image content (" + image.width() + "x" + image.height() + ")";
                } else if (c instanceof VideoContent video) {
                    return "Video content (" + video.durationSeconds() + " seconds)";
                } else if (c instanceof EmptyContent) {
                    return "Empty content";
                } else {
                    return "Unknown content type";
                }
            }).orElse("No content");
        }

        // Switch expression with pattern matching (Java 17 compatible)
        public String getContentDescription() {
            return content.map(c -> {
                if (c instanceof TextContent text) {
                    return "Text: " + (text.text().length() > 50 ? text.text().substring(0, 50) + "..." : text.text()) + 
                           " (" + text.wordCount() + " words)";
                } else if (c instanceof ImageContent image) {
                    return "Image: " + image.altText() + " [" + image.width() + "x" + image.height() + "] at " + image.imageUrl();
                } else if (c instanceof VideoContent video) {
                    return "Video: " + video.durationSeconds() + "s" + (video.hasSubtitles() ? " (with subtitles)" : "") + " at " + video.videoUrl();
                } else if (c instanceof EmptyContent) {
                    return "Empty page content";
                } else {
                    return "Unknown content type";
                }
            }).orElse("No content available");
        }

        // Optional handling patterns
        public String getAuthorInfo() {
            return author
                .map(name -> "Author: " + name)
                .orElse("Author unknown");
        }

        public Optional<String> getPublicationStatus() {
            if (!isPublished) {
                return Optional.empty();
            }
            
            return author
                .map(name -> "Published by " + name + " (v" + version + ")")
                .or(() -> Optional.of("Published anonymously (v" + version + ")"));
        }

        // Pattern matching with guards
        public boolean isLongContent() {
            return content
                .filter(c -> c instanceof TextContent text && text.wordCount() > 1000)
                .isPresent() ||
            content
                .filter(c -> c instanceof VideoContent video && video.durationSeconds() > 300)
                .isPresent();
        }

        // Complex pattern matching example (Java 17 compatible)
        public String getContentSummary() {
            return content.map(c -> {
                if (c instanceof TextContent text) {
                    if (text.wordCount() > 500) {
                        return "Long text content: " + text.text().substring(0, Math.min(100, text.text().length())) + "...";
                    } else {
                        return "Short text content: " + text.text();
                    }
                } else if (c instanceof ImageContent image) {
                    if (image.width() * image.height() > 1000000) {
                        return "High resolution image: " + image.altText();
                    } else {
                        return "Standard image: " + image.altText();
                    }
                } else if (c instanceof VideoContent video) {
                    if (video.durationSeconds() > 3600) {
                        return "Long video content" + (video.hasSubtitles() ? " with subtitles" : "");
                    } else {
                        return "Video content" + (video.hasSubtitles() ? " with subtitles" : "");
                    }
                } else if (c instanceof EmptyContent) {
                    return "No content";
                } else {
                    return "Unknown content type";
                }
            }).orElse("Page has no content");
        }
    }

    private Page textPage;
    private Page imagePage;
    private Page videoPage;
    private Page emptyPage;
    private Page pageWithoutAuthor;

    @BeforeEach
    void setUp() {
        textPage = new Page(
            "Sample Text Page",
            new TextContent("This is a sample text content for testing purposes. It contains multiple sentences and should have a reasonable word count for testing pattern matching and Optional usage in our Page class.", 32),
            "John Doe",
            true,
            1
        );

        imagePage = new Page(
            "Image Gallery",
            new ImageContent("https://example.com/image.jpg", "Sample image", 800, 600), // 480,000 pixels - standard res
            "Jane Smith",
            true,
            2
        );

        videoPage = new Page(
            "Video Tutorial",
            new VideoContent("https://example.com/video.mp4", 450, true),
            "Bob Johnson",
            false,
            1
        );

        emptyPage = new Page(
            "Empty Page",
            new EmptyContent(),
            null,
            false,
            1
        );

        pageWithoutAuthor = new Page(
            "Anonymous Page",
            new TextContent("Anonymous content", 2),
            null,
            true,
            3
        );
    }

    @Test
    @DisplayName("Should demonstrate pattern matching for instanceof")
    void testPatternMatchingInstanceof() {
        assertEquals("Text content with 32 words", textPage.getContentType());
        assertEquals("Image content (800x600)", imagePage.getContentType());
        assertEquals("Video content (450 seconds)", videoPage.getContentType());
        assertEquals("Empty content", emptyPage.getContentType());
    }

    @Test
    @DisplayName("Should demonstrate switch expressions with pattern matching (Java 17)")
    void testSwitchExpressionPatternMatching() {
        assertTrue(textPage.getContentDescription().startsWith("Text:"));
        assertTrue(textPage.getContentDescription().contains("(32 words)"));
        
        assertEquals("Image: Sample image [800x600] at https://example.com/image.jpg", 
                    imagePage.getContentDescription());
        
        assertEquals("Video: 450s (with subtitles) at https://example.com/video.mp4", 
                    videoPage.getContentDescription());
        
        assertEquals("Empty page content", emptyPage.getContentDescription());
    }

    @Test
    @DisplayName("Should handle Optional values correctly")
    void testOptionalHandling() {
        // Test present Optional
        assertEquals("Author: John Doe", textPage.getAuthorInfo());
        assertEquals("Author: Jane Smith", imagePage.getAuthorInfo());
        
        // Test empty Optional
        assertEquals("Author unknown", emptyPage.getAuthorInfo());
        assertEquals("Author unknown", pageWithoutAuthor.getAuthorInfo());
    }

    @Test
    @DisplayName("Should demonstrate Optional with complex logic")
    void testOptionalPublicationStatus() {
        // Published with author
        Optional<String> textStatus = textPage.getPublicationStatus();
        assertTrue(textStatus.isPresent());
        assertEquals("Published by John Doe (v1)", textStatus.get());
        
        // Published without author
        Optional<String> anonymousStatus = pageWithoutAuthor.getPublicationStatus();
        assertTrue(anonymousStatus.isPresent());
        assertEquals("Published anonymously (v3)", anonymousStatus.get());
        
        // Not published
        Optional<String> unpublishedStatus = videoPage.getPublicationStatus();
        assertFalse(unpublishedStatus.isPresent());
        
        Optional<String> emptyStatus = emptyPage.getPublicationStatus();
        assertFalse(emptyStatus.isPresent());
    }

    @Test
    @DisplayName("Should use pattern matching with conditional logic")
    void testPatternMatchingWithGuards() {
        // Long text content (> 1000 words)
        Page longTextPage = new Page(
            "Long Article",
            new TextContent("Very long content...", 1500),
            "Author",
            true,
            1
        );
        assertTrue(longTextPage.isLongContent());
        
        // Long video content (> 300 seconds)
        assertTrue(videoPage.isLongContent()); // 450 seconds
        
        // Short content
        assertFalse(textPage.isLongContent()); // 32 words
        assertFalse(imagePage.isLongContent()); // Image doesn't count as long
        assertFalse(emptyPage.isLongContent()); // Empty content
    }

    @Test
    @DisplayName("Should handle pattern matching with conditional clauses")
    void testPatternMatchingWithWhen() {
        // Test short text
        assertTrue(textPage.getContentSummary().startsWith("Short text content:"));
        
        // Test long text
        Page longTextPage = new Page(
            "Long Article",
            new TextContent("This is a very long text content that exceeds 500 words and should trigger the long content pattern matching case with appropriate truncation of the text content for display purposes.", 600),
            "Author",
            true,
            1
        );
        assertTrue(longTextPage.getContentSummary().startsWith("Long text content:"));
        assertTrue(longTextPage.getContentSummary().endsWith("..."));
        
        // Test high resolution image
        Page highResPage = new Page(
            "High Res Image",
            new ImageContent("https://example.com/hires.jpg", "High resolution image", 2000, 1500),
            "Photographer",
            true,
            1
        );
        assertEquals("High resolution image: High resolution image", highResPage.getContentSummary());
        
        // Test standard image
        assertEquals("Standard image: Sample image", imagePage.getContentSummary());
        
        // Test long video
        Page longVideoPage = new Page(
            "Long Video",
            new VideoContent("https://example.com/long.mp4", 4000, false),
            "Creator",
            true,
            1
        );
        assertEquals("Long video content", longVideoPage.getContentSummary());
        
        // Test standard video
        assertEquals("Video content with subtitles", videoPage.getContentSummary());
        
        // Test empty content
        assertEquals("No content", emptyPage.getContentSummary());
    }

    @Test
    @DisplayName("Should validate record constraints in pattern matching")
    void testRecordValidation() {
        // Test TextContent validation
        assertThrows(NullPointerException.class, () -> {
            new TextContent(null, 10);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new TextContent("Valid text", -1);
        });
        
        // Test ImageContent validation
        assertThrows(NullPointerException.class, () -> {
            new ImageContent(null, "Alt text", 100, 100);
        });
        
        assertThrows(NullPointerException.class, () -> {
            new ImageContent("https://example.com/image.jpg", null, 100, 100);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new ImageContent("https://example.com/image.jpg", "Alt text", -1, 100);
        });
        
        // Test VideoContent validation
        assertThrows(NullPointerException.class, () -> {
            new VideoContent(null, 100, false);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new VideoContent("https://example.com/video.mp4", -1, false);
        });
    }

    @Test
    @DisplayName("Should demonstrate Optional chaining")
    void testOptionalChaining() {
        // Test Optional chaining with map and filter
        Optional<Integer> wordCount = textPage.getContent()
            .filter(c -> c instanceof TextContent)
            .map(c -> (TextContent) c)
            .map(TextContent::wordCount);
        
        assertTrue(wordCount.isPresent());
        assertEquals(32, wordCount.get());
        
        // Test Optional chaining with empty result
        Optional<Integer> imageWordCount = imagePage.getContent()
            .filter(c -> c instanceof TextContent)
            .map(c -> (TextContent) c)
            .map(TextContent::wordCount);
        
        assertFalse(imageWordCount.isPresent());
    }

    @Test
    @DisplayName("Should handle sealed interface exhaustiveness")
    void testSealedInterfaceExhaustiveness() {
        // All PageContent implementations should be handled
        List<PageContent> allContentTypes = List.of(
            new TextContent("Test", 5),
            new ImageContent("url", "alt", 100, 100),
            new VideoContent("url", 60, false),
            new EmptyContent()
        );
        
        for (PageContent content : allContentTypes) {
            Page page = new Page("Test", content, "Author", true, 1);
            String description = page.getContentDescription();
            assertNotNull(description);
            assertFalse(description.isEmpty());
        }
    }
}