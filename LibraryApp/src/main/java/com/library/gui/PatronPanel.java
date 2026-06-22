/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.gui;

import com.library.exceptions.PatronHasActiveLoansException;
import com.library.models.Loan;
import com.library.models.Patron;
import com.library.services.AuthManager;
import com.library.services.LibraryManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatronPanel extends JPanel {
    private LibraryManager libraryManager;
    private AuthManager authManager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PatronPanel(LibraryManager libraryManager, AuthManager authManager) {
        this.libraryManager = libraryManager;
        this.authManager = authManager;
        setLayout(new BorderLayout());
        initUI();
        refreshTable();
    }

    private void initUI() {
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

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact Info"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton historyButton = new JButton("View History");
        historyButton.addActionListener(e -> showHistoryDialog());

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddEditDialog(null));
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> {
            Patron selected = getSelectedPatron();
            if (selected != null) showAddEditDialog(selected);
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedPatron());

        bottomPanel.add(historyButton);
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        if (authManager.isAdmin()) {
            bottomPanel.add(deleteButton);
        }

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void performSearch() {
        String query = searchField.getText();
        List<Patron> results = libraryManager.searchPatrons(query);
        populateTable(results);
    }

    public void refreshTable() {
        populateTable(libraryManager.getPatrons());
    }

    private void populateTable(List<Patron> patrons) {
        tableModel.setRowCount(0);
        for (Patron p : patrons) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getContactInfo()});
        }
    }

    private Patron getSelectedPatron() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patron.");
            return null;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        return libraryManager.getPatrons().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    private void deleteSelectedPatron() {
        Patron selected = getSelectedPatron();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete " + selected.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    libraryManager.deletePatron(selected);
                    refreshTable();
                } catch (PatronHasActiveLoansException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showAddEditDialog(Patron patronToEdit) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), patronToEdit == null ? "Add Patron" : "Edit Patron", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField(patronToEdit != null ? patronToEdit.getId() : "");
        if (patronToEdit != null) idField.setEditable(false);
        JTextField nameField = new JTextField(patronToEdit != null ? patronToEdit.getName() : "");
        JTextField contactField = new JTextField(patronToEdit != null ? patronToEdit.getContactInfo() : "");

        panel.add(new JLabel("ID:")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Contact Info:")); panel.add(contactField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            String contact = contactField.getText();

            if (patronToEdit == null) {
                libraryManager.addPatron(new Patron(id, name, contact));
            } else {
                libraryManager.updatePatron(patronToEdit, new Patron(id, name, contact));
            }
            refreshTable();
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showHistoryDialog() {
        Patron selected = getSelectedPatron();
        if (selected == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Borrowing History - " + selected.getName(), true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        List<Loan> history = libraryManager.getBorrowingHistory(selected);
        DefaultTableModel histModel = new DefaultTableModel(new String[]{"Book", "Checkout", "Due", "Returned", "Fine"}, 0);
        for (Loan l : history) {
            histModel.addRow(new Object[]{
                l.getBook().getTitle(),
                l.getCheckoutDate(),
                l.getDueDate(),
                l.getReturnDate() == null ? "Active" : l.getReturnDate(),
                "$" + l.getFineAmount()
            });
        }

        JTable histTable = new JTable(histModel);
        dialog.add(new JScrollPane(histTable), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel bp = new JPanel();
        bp.add(closeBtn);
        dialog.add(bp, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
