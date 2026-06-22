/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.services;

import com.library.exceptions.BookAlreadyOnLoanException;
import com.library.exceptions.PatronHasActiveLoansException;
import com.library.models.Book;
import com.library.models.Loan;
import com.library.models.Patron;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryManagerTest {

    private LibraryManager libraryManager;

    @BeforeEach
    public void setup() {
        libraryManager = new LibraryManager();
        // Clear data for fresh tests
        libraryManager.getBooks().clear();
        libraryManager.getPatrons().clear();
        // active loans clear indirectly via not saving them initially, 
        // but let's just test basic logic.
    }

    @Test
    public void testAddAndSearchBook() {
        Book b = new Book("Effective Java", "Joshua Bloch", "123", 2);
        libraryManager.addBook(b);
        assertEquals(1, libraryManager.searchBooks("Effective").size());
    }

    @Test
    public void testCheckoutBookSuccess() throws BookAlreadyOnLoanException {
        Book b = new Book("Clean Code", "Robert C. Martin", "456", 1);
        Patron p = new Patron("P1", "John", "john@email.com");
        
        libraryManager.addBook(b);
        libraryManager.addPatron(p);

        Loan loan = libraryManager.checkoutBook(b, p);
        
        assertNotNull(loan);
        assertEquals(0, b.getAvailableCopies());
    }

    @Test
    public void testCheckoutBookFailsNoCopies() throws BookAlreadyOnLoanException {
        Book b = new Book("Clean Code", "Robert C. Martin", "456", 1);
        Patron p1 = new Patron("P1", "John", "john@email.com");
        Patron p2 = new Patron("P2", "Jane", "jane@email.com");

        libraryManager.addBook(b);
        libraryManager.checkoutBook(b, p1);

        assertThrows(BookAlreadyOnLoanException.class, () -> {
            libraryManager.checkoutBook(b, p2);
        });
    }

    @Test
    public void testReturnBookWithFine() throws BookAlreadyOnLoanException {
        Book b = new Book("Refactoring", "Martin Fowler", "789", 1);
        Patron p = new Patron("P1", "Alice", "alice@email.com");
        libraryManager.addBook(b);
        
        Loan loan = libraryManager.checkoutBook(b, p);
        
        // Simulate returning 2 days late
        LocalDate returnDate = loan.getDueDate().plusDays(2);
        libraryManager.returnBook(loan, returnDate);
        
        assertEquals(1, b.getAvailableCopies());
        assertEquals(3.0, loan.getFineAmount()); // 2 days * 1.50
    }
}
