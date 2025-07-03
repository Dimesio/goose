package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;
import java.util.List;

/**
 * Tests for Customers sealed interface demonstrating pattern matching and sealed types
 * This simulates customer management functionality that might be used in the goose system
 */
public class CustomersTest {

    @Test
    @DisplayName("Should use pattern matching with sealed interfaces")
    void shouldUsePatternMatchingWithSealedInterfaces() {
        // Given
        List<Customer> customers = List.of(
            new IndividualCustomer("John", "Doe", "john.doe@example.com"),
            new BusinessCustomer("Acme Corp", "123456789", "contact@acme.com"),
            new PremiumCustomer("Jane", "Smith", "jane.smith@premium.com", "GOLD")
        );

        // When & Then - using traditional instanceof checks for Java 17 compatibility
        for (Customer customer : customers) {
            String customerInfo;
            if (customer instanceof IndividualCustomer) {
                IndividualCustomer ic = (IndividualCustomer) customer;
                customerInfo = String.format("Individual: %s %s (%s)", ic.firstName(), ic.lastName(), ic.email());
            } else if (customer instanceof BusinessCustomer) {
                BusinessCustomer bc = (BusinessCustomer) customer;
                customerInfo = String.format("Business: %s (Tax ID: %s, Email: %s)", bc.name(), bc.taxId(), bc.email());
            } else if (customer instanceof PremiumCustomer) {
                PremiumCustomer pc = (PremiumCustomer) customer;
                customerInfo = String.format("Premium %s: %s %s (%s)", pc.tier(), pc.firstName(), pc.lastName(), pc.email());
            } else {
                customerInfo = "Unknown customer type";
            }

            if (customer instanceof IndividualCustomer) {
                assertThat(customerInfo).contains("Individual: John Doe");
            } else if (customer instanceof BusinessCustomer) {
                assertThat(customerInfo).contains("Business: Acme Corp");
            } else if (customer instanceof PremiumCustomer) {
                assertThat(customerInfo).contains("Premium GOLD: Jane Smith");
            }
        }
    }

    @Test
    @DisplayName("Should calculate discount using pattern matching")
    void shouldCalculateDiscountUsingPatternMatching() {
        // Given
        List<Customer> customers = List.of(
            new IndividualCustomer("John", "Doe", "john.doe@example.com"),
            new BusinessCustomer("Acme Corp", "123456789", "contact@acme.com"),
            new PremiumCustomer("Jane", "Smith", "jane.smith@premium.com", "GOLD"),
            new PremiumCustomer("Bob", "Wilson", "bob.wilson@premium.com", "PLATINUM")
        );

        // When & Then
        for (Customer customer : customers) {
            double discount = calculateDiscount(customer);

            if (customer instanceof IndividualCustomer) {
                assertThat(discount).isEqualTo(0.05);
            } else if (customer instanceof BusinessCustomer) {
                assertThat(discount).isEqualTo(0.10);
            } else if (customer instanceof PremiumCustomer) {
                PremiumCustomer pc = (PremiumCustomer) customer;
                if ("GOLD".equals(pc.tier())) {
                    assertThat(discount).isEqualTo(0.15);
                } else if ("PLATINUM".equals(pc.tier())) {
                    assertThat(discount).isEqualTo(0.20);
                }
            } else {
                fail("Unexpected customer type");
            }
        }
    }

    @Test
    @DisplayName("Should validate customer data using pattern matching")
    void shouldValidateCustomerDataUsingPatternMatching() {
        // Given
        List<Customer> customers = List.of(
            new IndividualCustomer("", "Doe", "john.doe@example.com"), // Invalid: empty first name
            new BusinessCustomer("Acme Corp", "", "contact@acme.com"), // Invalid: empty tax ID
            new PremiumCustomer("Jane", "Smith", "invalid-email", "GOLD"), // Invalid: bad email
            new IndividualCustomer("Valid", "Customer", "valid@example.com") // Valid
        );

        // When & Then
        for (Customer customer : customers) {
            boolean isValid = validateCustomer(customer);

            if (customer instanceof IndividualCustomer) {
                IndividualCustomer ic = (IndividualCustomer) customer;
                if (ic.firstName().isEmpty()) {
                    assertThat(isValid).isFalse();
                } else if ("Valid".equals(ic.firstName())) {
                    assertThat(isValid).isTrue();
                } else {
                    assertThat(isValid).isFalse();
                }
            } else if (customer instanceof BusinessCustomer) {
                BusinessCustomer bc = (BusinessCustomer) customer;
                if (bc.taxId().isEmpty()) {
                    assertThat(isValid).isFalse();
                } else {
                    assertThat(isValid).isFalse();
                }
            } else if (customer instanceof PremiumCustomer) {
                PremiumCustomer pc = (PremiumCustomer) customer;
                if (!pc.email().contains("@")) {
                    assertThat(isValid).isFalse();
                } else {
                    assertThat(isValid).isFalse();
                }
            } else {
                assertThat(isValid).isFalse();
            }
        }
    }

    @Test
    @DisplayName("Should format customer display name using pattern matching")
    void shouldFormatCustomerDisplayNameUsingPatternMatching() {
        // Given
        Customer individual = new IndividualCustomer("John", "Doe", "john.doe@example.com");
        Customer business = new BusinessCustomer("Acme Corp", "123456789", "contact@acme.com");
        Customer premium = new PremiumCustomer("Jane", "Smith", "jane.smith@premium.com", "GOLD");

        // When & Then
        String individualName = formatDisplayName(individual);
        String businessName = formatDisplayName(business);
        String premiumName = formatDisplayName(premium);

        assertThat(individualName).isEqualTo("John Doe");
        assertThat(businessName).isEqualTo("Acme Corp");
        assertThat(premiumName).isEqualTo("Jane Smith [GOLD]");
    }

    @Test
    @DisplayName("Should handle customer operations with sealed types")
    void shouldHandleCustomerOperationsWithSealedTypes() {
        // Given
        Customer individual = new IndividualCustomer("John", "Doe", "john.doe@example.com");
        Customer business = new BusinessCustomer("Acme Corp", "123456789", "contact@acme.com");
        Customer premium = new PremiumCustomer("Jane", "Smith", "jane.smith@premium.com", "GOLD");

        // When & Then
        assertThat(canAccessPremiumFeatures(individual)).isFalse();
        assertThat(canAccessPremiumFeatures(business)).isFalse();
        assertThat(canAccessPremiumFeatures(premium)).isTrue();

        assertThat(getCustomerCategory(individual)).isEqualTo("RETAIL");
        assertThat(getCustomerCategory(business)).isEqualTo("COMMERCIAL");
        assertThat(getCustomerCategory(premium)).isEqualTo("VIP");
    }

    @Test
    @DisplayName("Should handle null safety with pattern matching")
    void shouldHandleNullSafetyWithPatternMatching() {
        // Given
        Customer nullCustomer = null;
        Customer validCustomer = new IndividualCustomer("John", "Doe", "john.doe@example.com");

        // When & Then
        String nullResult = safeFormatCustomer(nullCustomer);
        String validResult = safeFormatCustomer(validCustomer);

        assertThat(nullResult).isEqualTo("Unknown Customer");
        assertThat(validResult).contains("John Doe");
    }

    // Helper methods demonstrating pattern matching usage

    private double calculateDiscount(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            return 0.05; // 5% discount
        } else if (customer instanceof BusinessCustomer) {
            return 0.10; // 10% discount
        } else if (customer instanceof PremiumCustomer) {
            PremiumCustomer pc = (PremiumCustomer) customer;
            if ("GOLD".equals(pc.tier())) {
                return 0.15; // 15% discount
            } else if ("PLATINUM".equals(pc.tier())) {
                return 0.20; // 20% discount
            }
        }
        return 0.0;
    }

    private boolean validateCustomer(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer ic = (IndividualCustomer) customer;
            return !ic.firstName().isEmpty() && !ic.lastName().isEmpty() && ic.email().contains("@");
        } else if (customer instanceof BusinessCustomer) {
            BusinessCustomer bc = (BusinessCustomer) customer;
            return !bc.name().isEmpty() && !bc.taxId().isEmpty() && bc.email().contains("@");
        } else if (customer instanceof PremiumCustomer) {
            PremiumCustomer pc = (PremiumCustomer) customer;
            return !pc.firstName().isEmpty() && !pc.lastName().isEmpty() && pc.email().contains("@") && pc.tier() != null;
        }
        return false;
    }

    private String formatDisplayName(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            IndividualCustomer ic = (IndividualCustomer) customer;
            return String.format("%s %s", ic.firstName(), ic.lastName());
        } else if (customer instanceof BusinessCustomer) {
            BusinessCustomer bc = (BusinessCustomer) customer;
            return bc.name();
        } else if (customer instanceof PremiumCustomer) {
            PremiumCustomer pc = (PremiumCustomer) customer;
            return String.format("%s %s [%s]", pc.firstName(), pc.lastName(), pc.tier());
        }
        return "Unknown Customer";
    }

    private boolean canAccessPremiumFeatures(Customer customer) {
        return customer instanceof PremiumCustomer;
    }

    private String getCustomerCategory(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            return "RETAIL";
        } else if (customer instanceof BusinessCustomer) {
            return "COMMERCIAL";
        } else if (customer instanceof PremiumCustomer) {
            return "VIP";
        }
        return "UNKNOWN";
    }

    private String safeFormatCustomer(Customer customer) {
        if (customer == null) {
            return "Unknown Customer";
        } else if (customer instanceof IndividualCustomer) {
            IndividualCustomer ic = (IndividualCustomer) customer;
            return String.format("Individual: %s %s", ic.firstName(), ic.lastName());
        } else if (customer instanceof BusinessCustomer) {
            BusinessCustomer bc = (BusinessCustomer) customer;
            return String.format("Business: %s", bc.name());
        } else if (customer instanceof PremiumCustomer) {
            PremiumCustomer pc = (PremiumCustomer) customer;
            return String.format("Premium: %s %s (%s)", pc.firstName(), pc.lastName(), pc.tier());
        }
        return "Unknown Customer";
    }

    // Interface and record implementations - using regular interface for Java 17 compatibility
    public interface Customer {}

    public record IndividualCustomer(
        String firstName,
        String lastName,
        String email
    ) implements Customer {}

    public record BusinessCustomer(
        String name,
        String taxId,
        String email
    ) implements Customer {}

    public record PremiumCustomer(
        String firstName,
        String lastName,
        String email,
        String tier
    ) implements Customer {}
}