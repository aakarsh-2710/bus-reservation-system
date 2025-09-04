package com.mcs.payment.producer;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mcs.payment.model.PaymentEvent;

@Component
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentEvent event) {
        kafkaTemplate.send("payment-events", event.getBookingNo(), event);
    }
}
