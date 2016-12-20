package com.cep.api;

import akka.actor.ActorRef;
import com.cep.service.EsperAnomalyService;
import com.cep.service.FlightDelayPaxImpactAnomalyService;
import com.cep.service.domain.FlightEvent;
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
 * Helps to show case Cep use case by interacting with a Cep AKKA persistent actor that handles flight delay event.
 * <p>
 * 1. Can send flight delay event that gets send to the CEP actor
 * 2. That checks for anomalies for flight delay events and publishes anomalies.
 */
@RestController
@RequestMapping("/v1")
public class CepShowCaseFlightDelayRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(CepShowCaseFlightDelayRestApi.class);

    @Autowired
    private FlightDelayPaxImpactAnomalyService flightDelayPaxImpactAnomalyService;

    @Autowired
    private EsperAnomalyService esperAnomalyService;


    @RequestMapping(value = "flightDelayPaxImpactUsingPureAkka", method = RequestMethod.POST)
    public ResponseEntity<String> flightDelayPaxImpactUsingPureAkka(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("API received the delay flight event {}", flightEvent);

        flightDelayPaxImpactAnomalyService.handleFlightDelay(flightEvent, ActorRef.noSender());

        return new ResponseEntity<String>("Delayed Flight Event processed.", HttpStatus.OK);
    }

    @RequestMapping(value = "flightDelayUsingEsper", method = RequestMethod.POST)
    public ResponseEntity<String> flightDelayUsingEsper(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("API received the delay flight event {}", flightEvent);

        esperAnomalyService.handleFlightDelay(flightEvent, ActorRef.noSender());

        return new ResponseEntity<String>("Delayed Flight Event processed.", HttpStatus.OK);
    }

    @RequestMapping(value = "maintEventUsingEsper", method = RequestMethod.POST)
    public ResponseEntity<String> maintEventUsingEsper(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("API received {}", flightEvent);

        esperAnomalyService.handleMaintenanceMessage(flightEvent, ActorRef.noSender());

        return new ResponseEntity<String>("Event processed.", HttpStatus.OK);
    }

    @RequestMapping(value = "overrideEventUsingEsper", method = RequestMethod.POST)
    public ResponseEntity<String> overrideEventUsingEsper(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("API received {}", flightEvent);

        esperAnomalyService.handleOverrideEvent(flightEvent, ActorRef.noSender());

        return new ResponseEntity<String>("Event processed.", HttpStatus.OK);
    }

    @RequestMapping(value = "ping", method = RequestMethod.POST)
    public ResponseEntity<String> ping(@RequestBody FlightEvent flightEvent) throws Exception {

        LOGGER.info("ping received ");

        flightDelayPaxImpactAnomalyService.ping(flightEvent);

        return new ResponseEntity<String>("Ping performed.", HttpStatus.OK);

    }


}
