package com.cep.api;

import com.cep.service.dataservice.PnrService;
import com.cep.service.domain.PassengerOnFlightEvent;
import com.cep.service.domain.Pnr;
import com.cep.service.messagehandler.PassengerOnFlightEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Show cases how to integrate with Kafka and exposes a REST API
 * 1. REST API Which will help to send a passenger on flight event message to topic in Kafka Broker.
 * 2. And an Kafka internal lister listens for the send message and update the PNR cassandra tables.
 * <p>
 */
@RestController
@RequestMapping("/v1")
public class KafkaShowCasePassengerRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaShowCasePassengerRestApi.class);

    @Autowired
    private PnrService pnrService;

    @Autowired
    private PassengerOnFlightEventPublisher passengerOnFlightEventPublisher;


    /**
     * Send PassengerOnFlightEvent to the Kafka service for publishing.
     */
    @RequestMapping(value = "passengerOnFlightEvent", method = RequestMethod.POST)
    public ResponseEntity<String> savePassengerInFlightEvent(@RequestBody PassengerOnFlightEvent passengerOnFlightEvent) throws Exception {

        LOGGER.info("API received {}", passengerOnFlightEvent);

        passengerOnFlightEventPublisher.publish(passengerOnFlightEvent);

        return new ResponseEntity<String>("published PassengerOnFlightEvent.", HttpStatus.OK);
    }


    /**
     * Retrieve All flights by querying cassandra column family.
     */
    @RequestMapping(value = "pnrs", method = RequestMethod.GET)
    public ResponseEntity<List<Pnr>> pnrs() throws Exception {

        LOGGER.info("Retrieve all pnrs.");

        return new ResponseEntity<List<Pnr>>(pnrService.findAllPnrs(), HttpStatus.OK);
    }
}
