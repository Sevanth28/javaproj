package com.fraud.rules;

import com.fraud.model.Transaction;
import com.fraud.exception.SuspiciousTransactionException;

public class HighAmountRule implements FraudRule {

    @Override
    public void check(Transaction t) throws SuspiciousTransactionException {
        if (t.amount > 50000) {
            throw new SuspiciousTransactionException("High amount transaction");
        }
    }
}
