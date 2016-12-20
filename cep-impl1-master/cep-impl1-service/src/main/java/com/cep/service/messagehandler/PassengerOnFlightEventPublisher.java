package com.cep.service.messagehandler;

import com.cep.service.domain.PassengerOnFlightEvent;
import com.cep.service.property.KafkaProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Publish flight events.
 */

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka.passenger.on.flight.event")
public class PassengerOnFlightEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassengerOnFlightEventPublisher.class);

    @Autowired
    private KafkaProperties kafkaProperties;

    private Properties producerConfig = new Properties();

    private KafkaProducer<String, PassengerOnFlightEvent> kafkaProducer;

    public Properties getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(Properties producerConfig) {
        this.producerConfig = producerConfig;
    }

    @PostConstruct
    public void postConstruct() {
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getKafkaBootstrapServers());
        kafkaProducer = new KafkaProducer<>(producerConfig);
    }

    public void publish(PassengerOnFlightEvent passengerOnFlightEvent) {

        LOGGER.info("Publish event: {} ", passengerOnFlightEvent);

        kafkaProducer.send(new ProducerRecord<>(kafkaProperties.getPassengerOnFlightEventTopic(), kafkaProperties.getPassengerOnFlightEventTopicPartition(),
                passengerOnFlightEvent.confirmationNumber(), passengerOnFlightEvent));


    }


}
