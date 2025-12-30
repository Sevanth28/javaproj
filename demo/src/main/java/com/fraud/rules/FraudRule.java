package com.fraud.rules;

import com.fraud.model.Transaction;
import com.fraud.exception.SuspiciousTransactionException;

public interface FraudRule {
    void check(Transaction t) throws SuspiciousTransactionException;
}
