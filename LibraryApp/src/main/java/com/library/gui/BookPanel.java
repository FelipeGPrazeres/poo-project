/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.gui;

import com.library.models.Book;
import com.library.services.AuthManager;
import com.library.services.LibraryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
    private LibraryManager libraryManager;
    private AuthManager authManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public BookPanel(LibraryManager libraryManager, AuthManager authManager) {
        this.libraryManager = libraryManager;
        this.authManager = authManager;
        setLayout(new BorderLayout());
        initUI();
        refreshTable();
    }

    private void initUI() {
        // Top Panel: Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> { searchField.setText(""); refreshTable(); });

        Timer timer = new Timer(500, e -> performSearch());
        timer.setRepeats(false);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { timer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { timer.restart(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { timer.restart(); }
        });

        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearButton);
        add(topPanel, BorderLayout.NORTH);

        // Center: Table
        tableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN", "Total", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddEditDialog(null));
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            Book selected = getSelectedBook();
            if (selected != null) showAddEditDialog(selected);
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            Book selected = getSelectedBook();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete " + selected.getTitle() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    libraryManager.deleteBook(selected);
                    refreshTable();
                }
            }
        });

        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        
        // Only admin can delete books
        if (authManager.isAdmin()) {
            bottomPanel.add(deleteButton);
        }

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void performSearch() {
        String query = searchField.getText();
        List<Book> results = libraryManager.searchBooks(query);
        populateTable(results);
    }

    public void refreshTable() {
        populateTable(libraryManager.getBooks());
    }

    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getTitle(), b.getAuthor(), b.getIsbn(), b.getTotalCopies(), b.getAvailableCopies()});
        }
    }

    private Book getSelectedBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book.");
            return null;
        }
        String isbn = (String) tableModel.getValueAt(row, 2);
        return libraryManager.getBooks().stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    private void showAddEditDialog(Book bookToEdit) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), bookToEdit == null ? "Add Book" : "Edit Book", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField(bookToEdit != null ? bookToEdit.getTitle() : "");
        JTextField authorField = new JTextField(bookToEdit != null ? bookToEdit.getAuthor() : "");
        JTextField isbnField = new JTextField(bookToEdit != null ? bookToEdit.getIsbn() : "");
        if (bookToEdit != null) isbnField.setEditable(false); // cannot change ISBN once set
        JTextField copiesField = new JTextField(bookToEdit != null ? String.valueOf(bookToEdit.getTotalCopies()) : "");

        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Author:")); panel.add(authorField);
        panel.add(new JLabel("ISBN:")); panel.add(isbnField);
        panel.add(new JLabel("Total Copies:")); panel.add(copiesField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                String author = authorField.getText();
                String isbn = isbnField.getText();
                int copies = Integer.parseInt(copiesField.getText());

                if (bookToEdit == null) {
                    libraryManager.addBook(new Book(title, author, isbn, copies));
                } else {
                    Book updated = new Book(title, author, isbn, copies);
                    updated.setAvailableCopies(bookToEdit.getAvailableCopies() + (copies - bookToEdit.getTotalCopies()));
                    libraryManager.updateBook(bookToEdit, updated);
                }
                refreshTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number for copies.");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
