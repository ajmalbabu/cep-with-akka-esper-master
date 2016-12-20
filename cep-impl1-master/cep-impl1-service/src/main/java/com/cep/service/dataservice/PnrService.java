package com.cep.service.dataservice;


import com.cep.service.dao.FlightDao;
import com.cep.service.dao.PassengerDao;
import com.cep.service.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PnrService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PnrService.class);

    @Autowired
    private PassengerDao passengerDao;

    @Autowired
    private FlightDao flightDao;

    public List<Pnr> findAllPnrs() {
        return passengerDao.findAllPnrs();
    }


    public AffectedPnr findAffectedPnrs(FlightDelayMessage flightDelayMessage) {

        List<String> affectedPnrs = new ArrayList<>();

        Set<String> confirmationNumbers = flightDao.findPnrs(flightDelayMessage.getFlightInfo().flightKey());

        for (String confirmationNumber : confirmationNumbers) {

            List<String> flightKeys = passengerDao.findFlightKeys(confirmationNumber);

            List<FlightEvent> downLineFlights = flightDao.findFlightsAfter(flightKeys, flightDelayMessage.getFlightInfo().flightKey());

            if (affected(flightDelayMessage.getFlightInfo(), downLineFlights)) {
                affectedPnrs.add(confirmationNumber);
            }
        }


        return new AffectedPnr(affectedPnrs, flightDelayMessage.getFlightInfo().flightKey());
    }

    /**
     * Implement the actual business logic to decide if the current flight delay impacts the down-line flights.
     * Current logic only checks if the flight numbers changes for the down-line flights, if so those are impacted.
     *
     * @param currentFlight   - Delayed flight.
     * @param downLineFlights - Ordered list of flights passenger has to complete. The first flight in this list
     *                        is the current flight.
     * @return true if the delay causes affect on down-line flights.
     */
    private boolean affected(FlightInfo currentFlight, List<FlightEvent> downLineFlights) {

        boolean start = false;
        for (FlightEvent downLineFlight : downLineFlights) {
            if (currentFlight.flightKey().equals(downLineFlight.getFlightInfo().flightKey())) {
                start = true;
                continue;
            }
            if (start && currentFlight.getFlightNumber() != downLineFlight.getFlightInfo().getFlightNumber()) {
                return true;
            }
        }
        return false;
    }
}
