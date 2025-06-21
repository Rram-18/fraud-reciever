package com.ram.fraud_detection.repository;

import com.ram.fraud_detection.model.Fraud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudRepository extends JpaRepository<Fraud, Long> {}
// placeholder
