package com.ram.fraud_detection.controller;

import com.ram.fraud_detection.model.FraudAlert;
import com.ram.fraud_detection.repository.FraudAlertRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-alerts")
public class AlertController {
    private final FraudAlertRepository repo;

    public AlertController(FraudAlertRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<FraudAlert> getAllAlerts() {
        return repo.findAll();
    }
}
// placeholder
