/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.gui;

import com.library.exceptions.AuthenticationException;
import com.library.services.AuthManager;
import com.library.services.LibraryManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private AuthManager authManager;
    private LibraryManager libraryManager;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(AuthManager authManager, LibraryManager libraryManager) {
        this.authManager = authManager;
        this.libraryManager = libraryManager;

        setTitle("Library Management System - Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> attemptLogin());
        panel.add(new JLabel()); // Spacer
        panel.add(loginButton);

        add(panel);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            authManager.login(username, password);
            JOptionPane.showMessageDialog(this, "Login successful!");
            
            // Open MainFrame
            MainFrame mainFrame = new MainFrame(authManager, libraryManager);
            mainFrame.setVisible(true);
            
            this.dispose(); // Close login window
        } catch (AuthenticationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
