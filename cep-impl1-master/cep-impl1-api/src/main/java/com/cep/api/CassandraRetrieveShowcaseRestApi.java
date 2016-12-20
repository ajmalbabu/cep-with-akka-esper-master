package com.cep.api;

import com.cep.service.dataservice.FlightService;
import com.cep.service.domain.FlightEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expose REST api that shows how to retrieve some value from cassandra tables.
 * <p>
 * The event retrieved are flight events.
 */
@RestController
@RequestMapping("/v1")
public class CassandraRetrieveShowcaseRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraRetrieveShowcaseRestApi.class);

    @Autowired
    private FlightService flightService;


    /**
     * Retrieve All flights by querying cassandra column family.
     */
    @RequestMapping(value = "flightEvent", method = RequestMethod.GET)
    public ResponseEntity<List<FlightEvent>> flightEvents() throws Exception {

        LOGGER.info("Retrieve all flight events.");

        return new ResponseEntity<List<FlightEvent>>(flightService.findAllFlights(), HttpStatus.OK);
    }

}
