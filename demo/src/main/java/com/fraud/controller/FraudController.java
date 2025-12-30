package com.fraud.controller;

import com.fraud.model.Transaction;
import com.fraud.service.FraudService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class FraudController {

    private final FraudService service;

    public FraudController(FraudService service) {
        this.service = service;
    }

    @PostMapping("/transaction")
    public String submit(@RequestBody Transaction t) {
        return service.process(t);
    }

    @GetMapping("/status")
    public String status() {
        return service.status();
    }
}
