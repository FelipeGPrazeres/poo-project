/**
 * SCC0504 Project - Library Management
 * Felipe Galvão Prazeres: 16828948
 * Laura Nordi Zambom: 14655491
 */
package com.library.exceptions;

public class PatronHasActiveLoansException extends Exception {
    public PatronHasActiveLoansException(String message) {
        super(message);
    }
}
