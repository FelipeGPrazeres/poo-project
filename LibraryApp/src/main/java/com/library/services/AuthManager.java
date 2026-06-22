/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.services;

import com.library.data.FileManager;
import com.library.exceptions.AuthenticationException;
import com.library.models.User;

import java.util.List;

public class AuthManager {
    private List<User> users;
    private User loggedInUser;

    public AuthManager() {
        users = FileManager.loadUsers();
        if (users.isEmpty()) {
            // Create default admin
            users.add(new User("admin", "admin", User.Role.ADMINISTRATOR));
            users.add(new User("librarian", "lib", User.Role.LIBRARIAN));
            FileManager.saveUsers(users);
        }
    }

    public User login(String username, String password) throws AuthenticationException {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                loggedInUser = u;
                return u;
            }
        }
        throw new AuthenticationException("Invalid username or password.");
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getRole() == User.Role.ADMINISTRATOR;
    }
}
