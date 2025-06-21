package com.ram.fraud_detection.service;

import com.Transaction.model.Transaction;
import com.ram.fraud_detection.model.FraudAlert;
import com.ram.fraud_detection.repository.FraudAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FraudDetectionService {

    @Autowired
    private FraudAlertRepository alertRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${fraud.amount.threshold}")
    private double threshold;

    @KafkaListener(topics = "transactions", groupId = "fraud-group", containerFactory = "kafkaListenerContainerFactory")
    public void processTransaction(Transaction transaction) {
        boolean isFraud = false;
        StringBuilder reasons = new StringBuilder();

        // Rule 1: Check if amount exceeds threshold
        if (transaction.getAmount() > threshold) {
            isFraud = true;
            reasons.append("Amount exceeded ₹").append(threshold);
        }

        // Rule 2: Check if IP is outside India
        if (transaction.getIpAddress() != null && !transaction.getIpAddress().startsWith("192.168")) {
            try {
                String apiUrl = "https://ipapi.co/" + transaction.getIpAddress() + "/json/";
                Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
                String country = (String) response.get("country_name");

                if (country != null && !"India".equalsIgnoreCase(country)) {
                    isFraud = true;
                    if (reasons.length() > 0) reasons.append(", ");
                    reasons.append("IP from outside India (").append(country).append(")");
                }
            } catch (Exception e) {
                System.out.println("IP API failed: " + e.getMessage());
                // Optional: Log the error or fallback
            }
        }

        // Save fraud and send email if flagged
        if (isFraud) {
            FraudAlert alert = new FraudAlert();
            alert.setTransactionId(transaction.getTransactionId());
            alert.setUserId(transaction.getUserId());
            alert.setReason(reasons.toString());
            alert.setTimestamp(LocalDateTime.now());
            alertRepo.save(alert);

            String userEmail = "dubey.ram9919@gmail.com";
            emailService.sendAlertEmail(
                    userEmail,
                    "🚨 Fraud Detected",
                    "Transaction ID: " + transaction.getTransactionId() +
                            "\nAmount: ₹" + transaction.getAmount() +
                            "\nReason: " + reasons
            );
        }
    }
}
