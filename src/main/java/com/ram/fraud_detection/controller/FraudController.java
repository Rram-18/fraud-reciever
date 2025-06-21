package com.ram.fraud_detection.controller;

import com.ram.fraud_detection.model.Fraud;
import com.ram.fraud_detection.repository.FraudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/frauds")
public class FraudController {

    @Autowired
    private FraudRepository fraudRepository;

    @GetMapping
    public List<Fraud> getAllFrauds() {
        return fraudRepository.findAll();
    }

    @PostMapping
    public Fraud createFraud(@RequestBody Fraud fraud) {
        return fraudRepository.save(fraud);
    }
}
// placeholder
