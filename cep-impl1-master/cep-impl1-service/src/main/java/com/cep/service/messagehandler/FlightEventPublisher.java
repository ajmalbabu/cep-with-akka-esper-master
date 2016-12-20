package com.cep.service.messagehandler;

import com.cep.service.domain.FlightEvent;
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
@ConfigurationProperties(prefix = "kafka.flight.event")
public class FlightEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightEventPublisher.class);

    @Autowired
    private KafkaProperties kafkaProperties;

    private Properties producerConfig = new Properties();

    private KafkaProducer<String, FlightEvent> kafkaProducer;

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

    public void publish(FlightEvent flightEvent) {

        LOGGER.info("Publish flight event: {} ", flightEvent);

        kafkaProducer.send(new ProducerRecord<>(kafkaProperties.getFlightEventTopic(), kafkaProperties.getFlightEventTopicPartition(),
                flightEvent.flightKey(), flightEvent));


    }


}
