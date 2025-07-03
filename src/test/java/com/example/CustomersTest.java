package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.function.Function;

/**
 * Test class for Customers demonstrating sealed interfaces and pattern matching.
 * Showcases modern Java features including sealed classes/interfaces, 
 * pattern matching for switch expressions, and type-safe hierarchies.
 */
public class CustomersTest {

    // Sealed interface for customer types
    public sealed interface Customer 
        permits PremiumCustomer, RegularCustomer, GuestCustomer {
        
        String getId();
        String getName();
        String getEmail();
    }

    // Sealed interface for customer status
    public sealed interface CustomerStatus 
        permits ActiveStatus, InactiveStatus, SuspendedStatus {
    }

    public record ActiveStatus(String since, int loyaltyPoints) implements CustomerStatus {}
    public record InactiveStatus(String lastActivity) implements CustomerStatus {}
    public record SuspendedStatus(String reason, String until) implements CustomerStatus {}

    // Customer implementations
    public record PremiumCustomer(
        String id,
        String name,
        String email,
        double accountBalance,
        String tier,
        CustomerStatus status
    ) implements Customer {
        public PremiumCustomer {
            Objects.requireNonNull(id, "Customer ID cannot be null");
            Objects.requireNonNull(name, "Customer name cannot be null");
            Objects.requireNonNull(email, "Customer email cannot be null");
            Objects.requireNonNull(tier, "Premium tier cannot be null");
            Objects.requireNonNull(status, "Customer status cannot be null");
            if (accountBalance < 0) {
                throw new IllegalArgumentException("Account balance cannot be negative");
            }
        }

        @Override
        public String getId() { return id; }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getEmail() { return email; }
    }

    public record RegularCustomer(
        String id,
        String name,
        String email,
        int membershipMonths,
        CustomerStatus status
    ) implements Customer {
        public RegularCustomer {
            Objects.requireNonNull(id, "Customer ID cannot be null");
            Objects.requireNonNull(name, "Customer name cannot be null");
            Objects.requireNonNull(email, "Customer email cannot be null");
            Objects.requireNonNull(status, "Customer status cannot be null");
            if (membershipMonths < 0) {
                throw new IllegalArgumentException("Membership months cannot be negative");
            }
        }

        @Override
        public String getId() { return id; }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getEmail() { return email; }
    }

    public record GuestCustomer(
        String id,
        String name,
        String email,
        String sessionId,
        CustomerStatus status
    ) implements Customer {
        public GuestCustomer {
            Objects.requireNonNull(id, "Customer ID cannot be null");
            Objects.requireNonNull(name, "Customer name cannot be null");
            Objects.requireNonNull(email, "Customer email cannot be null");
            Objects.requireNonNull(sessionId, "Session ID cannot be null");
            Objects.requireNonNull(status, "Customer status cannot be null");
        }

        @Override
        public String getId() { return id; }
        
        @Override
        public String getName() { return name; }
        
        @Override
        public String getEmail() { return email; }
    }

    // Service class demonstrating pattern matching usage
    public static class CustomerService {
        
        // Pattern matching with if-else chains (Java 17 compatible)
        public String getCustomerType(Customer customer) {
            if (customer instanceof PremiumCustomer premium) {
                return "Premium Customer (" + premium.tier() + ") with balance: $" + premium.accountBalance();
            } else if (customer instanceof RegularCustomer regular) {
                return "Regular Customer (member for " + regular.membershipMonths() + " months)";
            } else if (customer instanceof GuestCustomer guest) {
                return "Guest Customer (session: " + guest.sessionId() + ")";
            } else {
                return "Unknown customer type";
            }
        }

        // Pattern matching with conditional logic (Java 17 compatible)
        public String getCustomerPriority(Customer customer) {
            if (customer instanceof PremiumCustomer premium) {
                if ("PLATINUM".equals(premium.tier())) {
                    return "HIGHEST";
                } else if ("GOLD".equals(premium.tier())) {
                    return "HIGH";
                } else {
                    return "MEDIUM";
                }
            } else if (customer instanceof RegularCustomer regular) {
                if (regular.membershipMonths() > 24) {
                    return "MEDIUM";
                } else {
                    return "LOW";
                }
            } else if (customer instanceof GuestCustomer) {
                return "LOWEST";
            } else {
                return "UNKNOWN";
            }
        }

        // Nested pattern matching with status (Java 17 compatible)
        public String getCustomerStatusDescription(Customer customer) {
            String customerType;
            if (customer instanceof PremiumCustomer premium) {
                customerType = "Premium " + premium.tier() + " customer: " + getStatusDetails(premium.status());
            } else if (customer instanceof RegularCustomer regular) {
                customerType = "Regular customer (member " + regular.membershipMonths() + "m): " + getStatusDetails(regular.status());
            } else if (customer instanceof GuestCustomer guest) {
                customerType = "Guest customer: " + getStatusDetails(guest.status());
            } else {
                customerType = "Unknown customer type";
            }
            return customerType;
        }

        private String getStatusDetails(CustomerStatus status) {
            if (status instanceof ActiveStatus active) {
                return "Active since " + active.since() + " (" + active.loyaltyPoints() + " loyalty points)";
            } else if (status instanceof InactiveStatus inactive) {
                return "Inactive since " + inactive.lastActivity();
            } else if (status instanceof SuspendedStatus suspended) {
                return "Suspended until " + suspended.until() + " (reason: " + suspended.reason() + ")";
            } else {
                return "Unknown status";
            }
        }

        // Complex pattern matching with business logic (Java 17 compatible)
        public boolean isEligibleForPromotion(Customer customer) {
            if (customer instanceof PremiumCustomer premium && premium.status() instanceof ActiveStatus active) {
                return active.loyaltyPoints() > 1000;
            } else if (customer instanceof RegularCustomer regular && regular.status() instanceof ActiveStatus active) {
                return regular.membershipMonths() > 12 && active.loyaltyPoints() > 500;
            } else {
                return false;
            }
        }

        // Pattern matching for instanceof with type extraction
        public Optional<String> getPremiumTier(Customer customer) {
            if (customer instanceof PremiumCustomer premium) {
                return Optional.of(premium.tier());
            }
            return Optional.empty();
        }

        // Pattern matching for data extraction (Java 17 compatible)
        public Map<String, Object> extractCustomerData(Customer customer) {
            if (customer instanceof PremiumCustomer premium) {
                return Map.of(
                    "type", "PREMIUM",
                    "id", premium.id(),
                    "name", premium.name(),
                    "email", premium.email(),
                    "balance", premium.accountBalance(),
                    "tier", premium.tier(),
                    "status", premium.status()
                );
            } else if (customer instanceof RegularCustomer regular) {
                return Map.of(
                    "type", "REGULAR",
                    "id", regular.id(),
                    "name", regular.name(),
                    "email", regular.email(),
                    "membershipMonths", regular.membershipMonths(),
                    "status", regular.status()
                );
            } else if (customer instanceof GuestCustomer guest) {
                return Map.of(
                    "type", "GUEST",
                    "id", guest.id(),
                    "name", guest.name(),
                    "email", guest.email(),
                    "sessionId", guest.sessionId(),
                    "status", guest.status()
                );
            } else {
                return Map.of("type", "UNKNOWN");
            }
        }
    }

    private CustomerService service;
    private List<Customer> testCustomers;

    @BeforeEach
    void setUp() {
        service = new CustomerService();
        
        testCustomers = List.of(
            new PremiumCustomer(
                "P001", "Alice Johnson", "alice@example.com", 
                5000.0, "PLATINUM", 
                new ActiveStatus("2023-01-01", 1500)
            ),
            new PremiumCustomer(
                "P002", "Bob Smith", "bob@example.com", 
                2000.0, "GOLD", 
                new ActiveStatus("2023-06-01", 800)
            ),
            new PremiumCustomer(
                "P003", "Charlie Brown", "charlie@example.com", 
                1000.0, "SILVER", 
                new SuspendedStatus("Payment issue", "2024-12-31")
            ),
            new RegularCustomer(
                "R001", "Diana Prince", "diana@example.com", 
                18, new ActiveStatus("2023-03-15", 600)
            ),
            new RegularCustomer(
                "R002", "Edward Norton", "edward@example.com", 
                6, new InactiveStatus("2024-10-01")
            ),
            new GuestCustomer(
                "G001", "Frank Miller", "frank@example.com", 
                "SESSION_12345", new ActiveStatus("2024-11-01", 0)
            )
        );
    }

    @Test
    @DisplayName("Should classify customer types using pattern matching")
    void testCustomerTypeClassification() {
        assertEquals("Premium Customer (PLATINUM) with balance: $5000.0", 
                    service.getCustomerType(testCustomers.get(0)));
        
        assertEquals("Premium Customer (GOLD) with balance: $2000.0", 
                    service.getCustomerType(testCustomers.get(1)));
        
        assertEquals("Regular Customer (member for 18 months)", 
                    service.getCustomerType(testCustomers.get(3)));
        
        assertEquals("Guest Customer (session: SESSION_12345)", 
                    service.getCustomerType(testCustomers.get(5)));
    }

    @Test
    @DisplayName("Should determine customer priority using pattern matching with guards")
    void testCustomerPriorityWithGuards() {
        // PLATINUM premium customer
        assertEquals("HIGHEST", service.getCustomerPriority(testCustomers.get(0)));
        
        // GOLD premium customer
        assertEquals("HIGH", service.getCustomerPriority(testCustomers.get(1)));
        
        // SILVER premium customer
        assertEquals("MEDIUM", service.getCustomerPriority(testCustomers.get(2)));
        
        // Regular customer with 18 months (< 24)
        assertEquals("LOW", service.getCustomerPriority(testCustomers.get(3)));
        
        // Guest customer
        assertEquals("LOWEST", service.getCustomerPriority(testCustomers.get(5)));
    }

    @Test
    @DisplayName("Should handle nested pattern matching with status")
    void testNestedPatternMatching() {
        String description = service.getCustomerStatusDescription(testCustomers.get(0));
        assertTrue(description.contains("Premium PLATINUM customer"));
        assertTrue(description.contains("Active since 2023-01-01"));
        assertTrue(description.contains("1500 loyalty points"));
        
        String suspendedDescription = service.getCustomerStatusDescription(testCustomers.get(2));
        assertTrue(suspendedDescription.contains("Premium SILVER customer"));
        assertTrue(suspendedDescription.contains("Suspended until 2024-12-31"));
        assertTrue(suspendedDescription.contains("reason: Payment issue"));
    }

    @Test
    @DisplayName("Should determine promotion eligibility using complex pattern matching")
    void testPromotionEligibility() {
        // Premium customer with > 1000 loyalty points
        assertTrue(service.isEligibleForPromotion(testCustomers.get(0)));
        
        // Premium customer with < 1000 loyalty points
        assertFalse(service.isEligibleForPromotion(testCustomers.get(1)));
        
        // Suspended premium customer
        assertFalse(service.isEligibleForPromotion(testCustomers.get(2)));
        
        // Regular customer with > 12 months and > 500 points
        assertTrue(service.isEligibleForPromotion(testCustomers.get(3)));
        
        // Regular customer with < 12 months
        assertFalse(service.isEligibleForPromotion(testCustomers.get(4)));
        
        // Guest customer
        assertFalse(service.isEligibleForPromotion(testCustomers.get(5)));
    }

    @Test
    @DisplayName("Should use pattern matching for instanceof with type extraction")
    void testPatternMatchingInstanceof() {
        // Premium customers
        assertEquals(Optional.of("PLATINUM"), service.getPremiumTier(testCustomers.get(0)));
        assertEquals(Optional.of("GOLD"), service.getPremiumTier(testCustomers.get(1)));
        assertEquals(Optional.of("SILVER"), service.getPremiumTier(testCustomers.get(2)));
        
        // Non-premium customers
        assertEquals(Optional.empty(), service.getPremiumTier(testCustomers.get(3)));
        assertEquals(Optional.empty(), service.getPremiumTier(testCustomers.get(4)));
        assertEquals(Optional.empty(), service.getPremiumTier(testCustomers.get(5)));
    }

    @Test
    @DisplayName("Should extract customer data using pattern matching")
    void testDataExtractionPatternMatching() {
        Map<String, Object> premiumData = service.extractCustomerData(testCustomers.get(0));
        assertEquals("PREMIUM", premiumData.get("type"));
        assertEquals("P001", premiumData.get("id"));
        assertEquals("Alice Johnson", premiumData.get("name"));
        assertEquals(5000.0, premiumData.get("balance"));
        assertEquals("PLATINUM", premiumData.get("tier"));
        
        Map<String, Object> regularData = service.extractCustomerData(testCustomers.get(3));
        assertEquals("REGULAR", regularData.get("type"));
        assertEquals("R001", regularData.get("id"));
        assertEquals(18, regularData.get("membershipMonths"));
        
        Map<String, Object> guestData = service.extractCustomerData(testCustomers.get(5));
        assertEquals("GUEST", guestData.get("type"));
        assertEquals("SESSION_12345", guestData.get("sessionId"));
    }

    @Test
    @DisplayName("Should validate sealed interface constraints")
    void testSealedInterfaceValidation() {
        // All Customer implementations should be handled in pattern matching
        for (Customer customer : testCustomers) {
            String type = service.getCustomerType(customer);
            assertNotNull(type);
            assertFalse(type.isEmpty());
            
            String priority = service.getCustomerPriority(customer);
            assertNotNull(priority);
            assertFalse(priority.isEmpty());
        }
    }

    @Test
    @DisplayName("Should validate record constraints")
    void testRecordValidation() {
        // Premium customer validation
        assertThrows(NullPointerException.class, () -> {
            new PremiumCustomer(null, "Name", "email@example.com", 1000.0, "GOLD", 
                new ActiveStatus("2023-01-01", 100));
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new PremiumCustomer("P001", "Name", "email@example.com", -100.0, "GOLD", 
                new ActiveStatus("2023-01-01", 100));
        });
        
        // Regular customer validation
        assertThrows(IllegalArgumentException.class, () -> {
            new RegularCustomer("R001", "Name", "email@example.com", -5, 
                new ActiveStatus("2023-01-01", 100));
        });
        
        // Guest customer validation
        assertThrows(NullPointerException.class, () -> {
            new GuestCustomer("G001", "Name", "email@example.com", null, 
                new ActiveStatus("2023-01-01", 0));
        });
    }

    @Test
    @DisplayName("Should handle all customer status types")
    void testCustomerStatusTypes() {
        ActiveStatus active = new ActiveStatus("2023-01-01", 500);
        InactiveStatus inactive = new InactiveStatus("2024-01-01");
        SuspendedStatus suspended = new SuspendedStatus("Violation", "2024-12-31");
        
        PremiumCustomer activeCustomer = new PremiumCustomer(
            "P100", "Active Customer", "active@example.com", 1000.0, "GOLD", active);
        PremiumCustomer inactiveCustomer = new PremiumCustomer(
            "P101", "Inactive Customer", "inactive@example.com", 1000.0, "GOLD", inactive);
        PremiumCustomer suspendedCustomer = new PremiumCustomer(
            "P102", "Suspended Customer", "suspended@example.com", 1000.0, "GOLD", suspended);
        
        String activeDesc = service.getCustomerStatusDescription(activeCustomer);
        assertTrue(activeDesc.contains("Active since 2023-01-01"));
        assertTrue(activeDesc.contains("500 loyalty points"));
        
        String inactiveDesc = service.getCustomerStatusDescription(inactiveCustomer);
        assertTrue(inactiveDesc.contains("Inactive since 2024-01-01"));
        
        String suspendedDesc = service.getCustomerStatusDescription(suspendedCustomer);
        assertTrue(suspendedDesc.contains("Suspended until 2024-12-31"));
        assertTrue(suspendedDesc.contains("reason: Violation"));
    }

    @Test
    @DisplayName("Should demonstrate exhaustive pattern matching")
    void testExhaustivePatternMatching() {
        // Create customers with all possible status combinations
        List<CustomerStatus> allStatuses = List.of(
            new ActiveStatus("2023-01-01", 100),
            new InactiveStatus("2024-01-01"),
            new SuspendedStatus("Test", "2024-12-31")
        );
        
        for (CustomerStatus status : allStatuses) {
            PremiumCustomer customer = new PremiumCustomer(
                "TEST", "Test Customer", "test@example.com", 1000.0, "GOLD", status);
            
            // All status types should be handled
            String description = service.getCustomerStatusDescription(customer);
            assertNotNull(description);
            assertFalse(description.isEmpty());
        }
    }
}