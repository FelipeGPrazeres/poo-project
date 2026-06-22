/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.services;

import com.library.exceptions.AuthenticationException;
import com.library.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthManagerTest {

    private AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    public void testSuccessfulLogin() throws AuthenticationException {
        User user = authManager.login("admin", "admin");
        assertNotNull(user);
        assertEquals(User.Role.ADMINISTRATOR, user.getRole());
        assertTrue(authManager.isAdmin());
    }

    @Test
    public void testFailedLogin() {
        assertThrows(AuthenticationException.class, () -> {
            authManager.login("invalid", "password");
        });
    }

    @Test
    public void testLogout() throws AuthenticationException {
        authManager.login("admin", "admin");
        authManager.logout();
        assertNull(authManager.getLoggedInUser());
        assertFalse(authManager.isAdmin());
    }
}
