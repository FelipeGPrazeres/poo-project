/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.data;

import com.library.models.Book;
import com.library.models.Loan;
import com.library.models.Patron;
import com.library.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private static final String BOOKS_FILE = DATA_DIR + "books.dat";
    private static final String PATRONS_FILE = DATA_DIR + "patrons.dat";
    private static final String LOANS_FILE = DATA_DIR + "loans.dat";
    private static final String USERS_FILE = DATA_DIR + "users.dat";

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveBooks(List<Book> books) { saveToFile(books, BOOKS_FILE); }
    public static List<Book> loadBooks() { return loadFromFile(BOOKS_FILE); }

    public static void savePatrons(List<Patron> patrons) { saveToFile(patrons, PATRONS_FILE); }
    public static List<Patron> loadPatrons() { return loadFromFile(PATRONS_FILE); }

    public static void saveLoans(List<Loan> loans) { saveToFile(loans, LOANS_FILE); }
    public static List<Loan> loadLoans() { return loadFromFile(LOANS_FILE); }

    public static void saveUsers(List<User> users) { saveToFile(users, USERS_FILE); }
    public static List<User> loadUsers() { return loadFromFile(USERS_FILE); }

    private static <T> void saveToFile(List<T> list, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.err.println("Error saving to file " + filePath + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from file " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
