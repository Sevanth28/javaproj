package com.fraud.service;

import com.sun.net.httpserver.HttpServer;
import com.fraud.model.Transaction;
import com.fraud.rules.*;
import com.fraud.exception.SuspiciousTransactionException;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;




public class FraudServer {

    // Location → time blocked
    static Map<String, Long> blockedLocations = new HashMap<>();

    // Transaction audit log
    static List<String> transactionHistory = new ArrayList<>();

    // Rule engine
    static List<FraudRule> rules = List.of(
        new HighAmountRule(),
        new RapidTransactionRule()
    );

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // ---------------- TRANSACTION API ----------------
        server.createContext("/transaction", exchange -> {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody()));
            String body = br.readLine();

            System.out.println("RAW BODY: " + body);

            String[] parts = body.split(",");
            double amount = Double.parseDouble(parts[0].trim());
            String location = parts[1].trim().toLowerCase();

            String response;

            // -------- AUTO-UNBLOCK CHECK --------
            if (blockedLocations.containsKey(location)) {
                long blockedAt = blockedLocations.get(location);
                if (System.currentTimeMillis() - blockedAt < 60000) {
                    response = "LOCATION TEMPORARILY BLOCKED: " + location;
                    send(exchange, response);
                    return;
                } else {
                    blockedLocations.remove(location);
                }
            }

            int riskScore = 0;

            Transaction t = new Transaction(amount, location);

            for (FraudRule rule : rules) {
                try {
                    rule.check(t);
                } catch (SuspiciousTransactionException e) {
                    riskScore += 50;
                }
            }

            if (riskScore >= 100) {
                blockedLocations.put(location, System.currentTimeMillis());
                response = "HIGH RISK → LOCATION BLOCKED";
            } else if (riskScore >= 50) {
                response = "SUSPICIOUS TRANSACTION";
            } else {
                response = "TRANSACTION APPROVED";
            }

            transactionHistory.add(
                    System.currentTimeMillis() + " | " +
                    amount + " | " +
                    location + " | " +
                    riskScore + " | " +
                    response
            );

            send(exchange, response);
        });

        // ---------------- HISTORY API ----------------
        server.createContext("/history", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String response = String.join("\n", transactionHistory);
            send(exchange, response);
        });

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("Fraud Detection Server running on port 8080");
    }

    // Utility method
    static void send(com.sun.net.httpserver.HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
