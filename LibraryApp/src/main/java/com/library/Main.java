/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library;

import com.library.gui.LoginFrame;
import com.library.services.AuthManager;
import com.library.services.LibraryManager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            AuthManager authManager = new AuthManager();
            LibraryManager libraryManager = new LibraryManager();
            
            LoginFrame loginFrame = new LoginFrame(authManager, libraryManager);
            loginFrame.setVisible(true);
        });
    }
}
