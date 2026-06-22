/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.gui;

import com.library.exceptions.BookAlreadyOnLoanException;
import com.library.models.Book;
import com.library.models.Loan;
import com.library.models.Patron;
import com.library.services.AuthManager;
import com.library.services.LibraryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class LoanPanel extends JPanel {
    private LibraryManager libraryManager;
    private AuthManager authManager;
    private JTable table;
    private DefaultTableModel tableModel;

    public LoanPanel(LibraryManager libraryManager, AuthManager authManager) {
        this.libraryManager = libraryManager;
        this.authManager = authManager;
        setLayout(new BorderLayout());
        initUI();
        refreshTable();
    }

    private void initUI() {
        // Table for Active Loans
        tableModel = new DefaultTableModel(new String[]{"Loan ID", "Book", "Patron", "Checkout", "Due"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        
        JButton checkoutBtn = new JButton("Checkout Book");
        checkoutBtn.addActionListener(e -> showCheckoutDialog());
        
        JButton returnBtn = new JButton("Return Book");
        returnBtn.addActionListener(e -> returnSelectedLoan());

        bottomPanel.add(refreshBtn);
        bottomPanel.add(checkoutBtn);
        bottomPanel.add(returnBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Loan> activeLoans = libraryManager.getActiveLoans();
        for (int i = 0; i < activeLoans.size(); i++) {
            Loan l = activeLoans.get(i);
            tableModel.addRow(new Object[]{
                l.getLoanId(),
                l.getBook().getTitle(),
                l.getPatron().getName(),
                l.getCheckoutDate().toString(),
                l.getDueDate().toString()
            });
        }
    }

    private Loan getSelectedLoan() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an active loan.");
            return null;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        
        List<Loan> activeLoans = libraryManager.getActiveLoans();
        for (int i = 0; i < activeLoans.size(); i++) {
            if (activeLoans.get(i).getLoanId().equals(id)) {
                return activeLoans.get(i);
            }
        }
        return null;
    }

    private void showCheckoutDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Checkout Book", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Book Selection Panel
        JPanel bookPanel = new JPanel(new BorderLayout(5, 5));
        bookPanel.setBorder(BorderFactory.createTitledBorder("Select Book"));
        JPanel bookSearchPanel = new JPanel(new BorderLayout());
        JTextField bookSearchField = new JTextField();
        JButton bookSearchBtn = new JButton("Search");
        JButton bookClearBtn = new JButton("Clear");
        bookClearBtn.addActionListener(e -> { bookSearchField.setText(""); bookSearchBtn.doClick(); });
        JPanel bookBtnPanel = new JPanel(new GridLayout(1, 2));
        bookBtnPanel.add(bookSearchBtn);
        bookBtnPanel.add(bookClearBtn);
        bookSearchPanel.add(bookSearchField, BorderLayout.CENTER);
        bookSearchPanel.add(bookBtnPanel, BorderLayout.EAST);
        
        Timer bookTimer = new Timer(500, e -> bookSearchBtn.doClick());
        bookTimer.setRepeats(false);
        bookSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { bookTimer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { bookTimer.restart(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { bookTimer.restart(); }
        });

        DefaultListModel<Book> bookListModel = new DefaultListModel<>();
        JList<Book> bookList = new JList<>(bookListModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Includes all books (even 0 copies) to show exception messages properly
        libraryManager.getBooks().forEach(bookListModel::addElement);
        
        bookSearchBtn.addActionListener(e -> {
            bookListModel.clear();
            String query = bookSearchField.getText().toLowerCase();
            for (Book b : libraryManager.getBooks()) {
                if (b.getTitle().toLowerCase().contains(query) || b.getAuthor().toLowerCase().contains(query) || b.getIsbn().toLowerCase().contains(query)) {
                    bookListModel.addElement(b);
                }
            }
        });
        
        bookPanel.add(bookSearchPanel, BorderLayout.NORTH);
        bookPanel.add(new JScrollPane(bookList), BorderLayout.CENTER);

        // Patron Selection Panel
        JPanel patronPanel = new JPanel(new BorderLayout(5, 5));
        patronPanel.setBorder(BorderFactory.createTitledBorder("Select Patron"));
        JPanel patronSearchPanel = new JPanel(new BorderLayout());
        JTextField patronSearchField = new JTextField();
        JButton patronSearchBtn = new JButton("Search");
        JButton patronClearBtn = new JButton("Clear");
        patronClearBtn.addActionListener(e -> { patronSearchField.setText(""); patronSearchBtn.doClick(); });
        JPanel patronBtnPanel = new JPanel(new GridLayout(1, 2));
        patronBtnPanel.add(patronSearchBtn);
        patronBtnPanel.add(patronClearBtn);
        patronSearchPanel.add(patronSearchField, BorderLayout.CENTER);
        patronSearchPanel.add(patronBtnPanel, BorderLayout.EAST);

        Timer patronTimer = new Timer(500, e -> patronSearchBtn.doClick());
        patronTimer.setRepeats(false);
        patronSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { patronTimer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { patronTimer.restart(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { patronTimer.restart(); }
        });
        
        DefaultListModel<Patron> patronListModel = new DefaultListModel<>();
        JList<Patron> patronList = new JList<>(patronListModel);
        patronList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        libraryManager.getPatrons().forEach(patronListModel::addElement);
        
        patronSearchBtn.addActionListener(e -> {
            patronListModel.clear();
            String query = patronSearchField.getText().toLowerCase();
            for (Patron p : libraryManager.getPatrons()) {
                if (p.getName().toLowerCase().contains(query) || p.getId().toLowerCase().contains(query) || p.getContactInfo().toLowerCase().contains(query)) {
                    patronListModel.addElement(p);
                }
            }
        });

        patronPanel.add(patronSearchPanel, BorderLayout.NORTH);
        patronPanel.add(new JScrollPane(patronList), BorderLayout.CENTER);

        mainPanel.add(bookPanel);
        mainPanel.add(patronPanel);

        // Bottom Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitBtn = new JButton("Checkout");
        submitBtn.addActionListener(e -> {
            Book selectedBook = bookList.getSelectedValue();
            Patron selectedPatron = patronList.getSelectedValue();

            if (selectedBook != null && selectedPatron != null) {
                try {
                    libraryManager.checkoutBook(selectedBook, selectedPatron);
                    JOptionPane.showMessageDialog(dialog, "Book checked out successfully!");
                    refreshTable();
                    dialog.dispose();
                } catch (BookAlreadyOnLoanException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select both a book and a patron.");
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void returnSelectedLoan() {
        Loan selected = getSelectedLoan();
        if (selected != null) {
            // Asking for return date to allow simulating late returns
            String dateInput = JOptionPane.showInputDialog(this, "Enter return date (YYYY-MM-DD) or leave empty for today:");
            if (dateInput == null) return; // User canceled

            LocalDate returnDate;
            try {
                if (dateInput.trim().isEmpty()) {
                    returnDate = LocalDate.now();
                } else {
                    returnDate = LocalDate.parse(dateInput);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }

            libraryManager.returnBook(selected, returnDate);
            
            String msg = "Book returned successfully.";
            if (selected.getFineAmount() > 0) {
                msg += "\nLate Fine: $" + String.format("%.2f", selected.getFineAmount());
            }
            JOptionPane.showMessageDialog(this, msg);
            refreshTable();
        }
    }
}
