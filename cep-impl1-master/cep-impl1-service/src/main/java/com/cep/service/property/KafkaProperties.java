package com.cep.service.property;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaProperties {

    @Value("${kafka.shutDownHook:false}")
    private boolean shutDownHook;

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String kafkaBootstrapServers;

    @Value("${kafka.passenger.on.flight.event.topic:passengerOnFlightEventTopic}")
    private String passengerOnFlightEventTopic;

    @Value("${kafka.passenger.on.flight.event.partition:0}")
    private int passengerOnFlightEventTopicPartition;

    @Value("${kafka.passenger.on.flight.event.pollIntervalMs:101}")
    private long passengerOnFlightPollIntervalMs;

    @Value("${kafka.flight.event.topic:flightEventTopic}")
    private String flightEventTopic;

    @Value("${kafka.flight.event.partition:0}")
    private int flightEventTopicPartition;

    @Value("${kafka.flight.event.pollIntervalMs:101}")
    private long flightPollIntervalMs;

    public boolean isShutDownHook() {
        return shutDownHook;
    }

    public void setShutDownHook(boolean shutDownHook) {
        this.shutDownHook = shutDownHook;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getPassengerOnFlightEventTopic() {
        return passengerOnFlightEventTopic;
    }

    public void setPassengerOnFlightEventTopic(String passengerOnFlightEventTopic) {
        this.passengerOnFlightEventTopic = passengerOnFlightEventTopic;
    }

    public int getPassengerOnFlightEventTopicPartition() {
        return passengerOnFlightEventTopicPartition;
    }

    public void setPassengerOnFlightEventTopicPartition(int passengerOnFlightEventTopicPartition) {
        this.passengerOnFlightEventTopicPartition = passengerOnFlightEventTopicPartition;
    }

    public long getPassengerOnFlightPollIntervalMs() {
        return passengerOnFlightPollIntervalMs;
    }

    public void setPassengerOnFlightPollIntervalMs(long passengerOnFlightPollIntervalMs) {
        this.passengerOnFlightPollIntervalMs = passengerOnFlightPollIntervalMs;
    }

    public String getFlightEventTopic() {
        return flightEventTopic;
    }

    public void setFlightEventTopic(String flightEventTopic) {
        this.flightEventTopic = flightEventTopic;
    }

    public int getFlightEventTopicPartition() {
        return flightEventTopicPartition;
    }

    public void setFlightEventTopicPartition(int flightEventTopicPartition) {
        this.flightEventTopicPartition = flightEventTopicPartition;
    }

    public long getFlightPollIntervalMs() {
        return flightPollIntervalMs;
    }

    public void setFlightPollIntervalMs(long flightPollIntervalMs) {
        this.flightPollIntervalMs = flightPollIntervalMs;
    }
}
