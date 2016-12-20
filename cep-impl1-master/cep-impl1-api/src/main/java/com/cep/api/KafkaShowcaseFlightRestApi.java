package com.cep.api;

import com.cep.service.domain.FlightEvent;
import com.cep.service.messagehandler.FlightEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Show cases how to integrate with Kafka and exposes a REST API
 * 1. REST API Which will help to send a flight event message to topic in Kafka Broker.
 * 2. And an Kafka internal lister listens for the send message and update the fight cassandra tables.
 * <p>
 */
@RestController
@RequestMapping("/v1")
public class KafkaShowcaseFlightRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaShowcaseFlightRestApi.class);

    @Autowired
    private FlightEventPublisher flightEventPublisher;

    /**
     * Send flightEvent to the Kafka service for publishing.
     */
    @RequestMapping(value = "flightEvent", method = RequestMethod.POST)
    public ResponseEntity<String> saveFlightEvent(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("API received the flight event {}", flightEvent);

        flightEventPublisher.publish(flightEvent);

        return new ResponseEntity<String>("Flight Event published.", HttpStatus.OK);
    }


}
