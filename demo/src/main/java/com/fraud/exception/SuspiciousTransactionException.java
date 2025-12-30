package com.fraud.exception;

public class SuspiciousTransactionException extends Exception {
    public SuspiciousTransactionException(String message) {
        super(message);
    }
}
