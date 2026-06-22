/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.services;

import com.library.data.FileManager;
import com.library.exceptions.BookAlreadyOnLoanException;
import com.library.exceptions.PatronHasActiveLoansException;
import com.library.models.Book;
import com.library.models.Loan;
import com.library.models.Patron;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LibraryManager {
    private List<Book> books;
    private List<Patron> patrons;
    private List<Loan> loans;

    private static final double DAILY_FINE_RATE = 1.50; // $1.50 per day overdue

    public LibraryManager() {
        books = FileManager.loadBooks();
        patrons = FileManager.loadPatrons();
        loans = FileManager.loadLoans();
    }

    // --- Book Management ---
    public void addBook(Book book) {
        books.add(book);
        saveData();
    }

    public void updateBook(Book oldBook, Book newBook) {
        int index = books.indexOf(oldBook);
        if (index != -1) {
            books.set(index, newBook);
            saveData();
        }
    }

    public void deleteBook(Book book) {
        books.remove(book);
        saveData();
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery) ||
                             b.getAuthor().toLowerCase().contains(lowerQuery) ||
                             b.getIsbn().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    // --- Patron Management ---
    public void addPatron(Patron patron) {
        patrons.add(patron);
        saveData();
    }

    public void updatePatron(Patron oldPatron, Patron newPatron) {
        int index = patrons.indexOf(oldPatron);
        if (index != -1) {
            patrons.set(index, newPatron);
            saveData();
        }
    }

    public void deletePatron(Patron patron) throws PatronHasActiveLoansException {
        boolean hasActiveLoans = loans.stream()
                .anyMatch(l -> l.getPatron().equals(patron) && l.isActive());
        
        if (hasActiveLoans) {
            throw new PatronHasActiveLoansException("Cannot delete patron with active loans.");
        }
        patrons.remove(patron);
        saveData();
    }

    public List<Patron> getPatrons() {
        return patrons;
    }

    public List<Patron> searchPatrons(String query) {
        String lowerQuery = query.toLowerCase();
        return patrons.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                             p.getId().toLowerCase().contains(lowerQuery) ||
                             p.getContactInfo().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    // --- Loan Management ---
    public Loan checkoutBook(Book book, Patron patron) throws BookAlreadyOnLoanException {
        if (book.getAvailableCopies() <= 0) {
            throw new BookAlreadyOnLoanException("No available copies of this book.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        
        LocalDate checkoutDate = LocalDate.now();
        LocalDate dueDate = checkoutDate.plusDays(14); // 14 days default
        String loanId = UUID.randomUUID().toString();

        Loan loan = new Loan(loanId, book, patron, checkoutDate, dueDate);
        loans.add(loan);
        saveData();
        
        return loan;
    }

    public void returnBook(Loan loan, LocalDate returnDate) {
        if (!loan.isActive()) return;

        loan.setReturnDate(returnDate);
        loan.getBook().setAvailableCopies(loan.getBook().getAvailableCopies() + 1);

        if (returnDate.isAfter(loan.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
            loan.setFineAmount(daysOverdue * DAILY_FINE_RATE);
        }

        saveData();
    }

    public List<Loan> getActiveLoans() {
        return loans.stream().filter(Loan::isActive).collect(Collectors.toList());
    }

    public List<Loan> getBorrowingHistory(Patron patron) {
        return loans.stream()
                .filter(l -> l.getPatron().equals(patron))
                .collect(Collectors.toList());
    }

    private void saveData() {
        FileManager.saveBooks(books);
        FileManager.savePatrons(patrons);
        FileManager.saveLoans(loans);
    }
}
