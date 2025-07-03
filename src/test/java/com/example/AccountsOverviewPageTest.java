package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tests for AccountsOverviewPage demonstrating immutability and list conversion
 * This simulates a UI component that might be used in the goose desktop application
 */
public class AccountsOverviewPageTest {

    private AccountsOverviewPage page;
    private List<Account> testAccounts;

    @BeforeEach
    void setUp() {
        testAccounts = List.of(
            new Account("1", "Primary Account", 1000.0, AccountType.CHECKING),
            new Account("2", "Savings Account", 5000.0, AccountType.SAVINGS),
            new Account("3", "Investment Account", 10000.0, AccountType.INVESTMENT)
        );
        page = new AccountsOverviewPage(testAccounts);
    }

    @Test
    @DisplayName("Should maintain immutability of account list")
    void shouldMaintainImmutabilityOfAccountList() {
        // Given
        List<Account> originalAccounts = page.getAccounts();
        int originalSize = originalAccounts.size();

        // When - attempt to modify the returned list
        assertThatThrownBy(() -> originalAccounts.add(new Account("4", "New Account", 100.0, AccountType.CHECKING)))
            .isInstanceOf(UnsupportedOperationException.class);

        // Then - original list should be unchanged
        assertThat(page.getAccounts()).hasSize(originalSize);
        assertThat(page.getAccounts()).isEqualTo(originalAccounts);
    }

    @Test
    @DisplayName("Should convert accounts to immutable list using modern Java")
    void shouldConvertAccountsToImmutableList() {
        // Given
        List<Account> mutableAccounts = new ArrayList<>(testAccounts);

        // When - create page with mutable list
        AccountsOverviewPage newPage = new AccountsOverviewPage(mutableAccounts);

        // Then - internal list should be immutable
        List<Account> pageAccounts = newPage.getAccounts();
        assertThatThrownBy(() -> pageAccounts.clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should filter accounts by type using streams")
    void shouldFilterAccountsByTypeUsingStreams() {
        // When
        List<Account> checkingAccounts = page.getAccountsByType(AccountType.CHECKING);
        List<Account> savingsAccounts = page.getAccountsByType(AccountType.SAVINGS);

        // Then
        assertThat(checkingAccounts)
            .hasSize(1)
            .extracting(Account::name)
            .containsExactly("Primary Account");

        assertThat(savingsAccounts)
            .hasSize(1)
            .extracting(Account::name)
            .containsExactly("Savings Account");
    }

    @Test
    @DisplayName("Should calculate total balance using streams")
    void shouldCalculateTotalBalanceUsingStreams() {
        // When
        double totalBalance = page.getTotalBalance();

        // Then
        assertThat(totalBalance).isEqualTo(16000.0);
    }

    @Test
    @DisplayName("Should convert to list using modern toList() method")
    void shouldConvertToListUsingModernToListMethod() {
        // When - use modern Java 16+ toList() instead of collect(Collectors.toList())
        List<String> accountNames = page.getAccounts()
            .stream()
            .map(Account::name)
            .toList(); // Modern Java approach

        // Then
        assertThat(accountNames)
            .containsExactly("Primary Account", "Savings Account", "Investment Account");

        // Verify immutability of result
        assertThatThrownBy(() -> accountNames.add("New Account"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should handle empty account list gracefully")
    void shouldHandleEmptyAccountListGracefully() {
        // Given
        AccountsOverviewPage emptyPage = new AccountsOverviewPage(List.of());

        // Then
        assertThat(emptyPage.getAccounts()).isEmpty();
        assertThat(emptyPage.getTotalBalance()).isZero();
        assertThat(emptyPage.getAccountsByType(AccountType.CHECKING)).isEmpty();
    }

    @Test
    @DisplayName("Should create defensive copy in constructor")
    void shouldCreateDefensiveCopyInConstructor() {
        // Given
        List<Account> mutableList = new ArrayList<>(testAccounts);
        AccountsOverviewPage newPage = new AccountsOverviewPage(mutableList);

        // When - modify original list
        mutableList.clear();

        // Then - page should still have original accounts
        assertThat(newPage.getAccounts()).hasSize(3);
        assertThat(newPage.getAccounts()).isNotEmpty();
    }

    // Supporting classes for testing
    public static final class AccountsOverviewPage {
        private final List<Account> accounts;

        public AccountsOverviewPage(List<Account> accounts) {
            // Create defensive copy and make immutable
            this.accounts = List.copyOf(accounts);
        }

        public List<Account> getAccounts() {
            return accounts; // Already immutable
        }

        public List<Account> getAccountsByType(AccountType type) {
            return accounts.stream()
                .filter(account -> account.type() == type)
                .toList(); // Modern Java 16+ approach
        }

        public double getTotalBalance() {
            return accounts.stream()
                .mapToDouble(Account::balance)
                .sum();
        }
    }

    public record Account(
        String id,
        String name,
        double balance,
        AccountType type
    ) {}

    public enum AccountType {
        CHECKING,
        SAVINGS,
        INVESTMENT
    }
}