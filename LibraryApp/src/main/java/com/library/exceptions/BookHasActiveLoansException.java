package com.library.exceptions;

public class BookHasActiveLoansException extends Exception {
    public BookHasActiveLoansException(String message) {
        super(message);
    }
}
