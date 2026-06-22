/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.gui;

import com.library.services.AuthManager;
import com.library.services.LibraryManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private AuthManager authManager;
    private LibraryManager libraryManager;

    public MainFrame(AuthManager authManager, LibraryManager libraryManager) {
        this.authManager = authManager;
        this.libraryManager = libraryManager;

        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel("Logged in as: " + authManager.getLoggedInUser().getUsername() + " (" + authManager.getLoggedInUser().getRole() + ")");
        headerPanel.add(userLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            authManager.logout();
            new LoginFrame(authManager, libraryManager).setVisible(true);
            this.dispose();
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Books", new BookPanel(libraryManager, authManager));
        tabbedPane.addTab("Patrons", new PatronPanel(libraryManager, authManager));
        tabbedPane.addTab("Loans", new LoanPanel(libraryManager, authManager));

        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof BookPanel) {
                ((BookPanel) selected).refreshTable();
            } else if (selected instanceof PatronPanel) {
                ((PatronPanel) selected).refreshTable();
            } else if (selected instanceof LoanPanel) {
                ((LoanPanel) selected).refreshTable();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }
}
