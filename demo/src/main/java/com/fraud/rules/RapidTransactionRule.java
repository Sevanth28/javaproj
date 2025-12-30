package com.fraud.rules;

import com.fraud.model.Transaction;
import com.fraud.exception.SuspiciousTransactionException;
import java.util.HashMap;
import java.util.Map;

public class RapidTransactionRule implements FraudRule {

    private static final Map<String, Long> lastTxnTime = new HashMap<>();

    @Override
    public void check(Transaction t) throws SuspiciousTransactionException {
        long now = System.currentTimeMillis();

        if (lastTxnTime.containsKey(t.location)) {
            long last = lastTxnTime.get(t.location);
            if (now - last < 5000) {
                throw new SuspiciousTransactionException("Rapid transactions detected");
            }
        }

        lastTxnTime.put(t.location, now);
    }
}
