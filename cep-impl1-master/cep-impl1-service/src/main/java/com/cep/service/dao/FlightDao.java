package com.cep.service.dao;

import com.cep.service.configuration.CassandraConfiguration;
import com.cep.service.domain.FlightEvent;
import com.cep.service.domain.FlightInfo;
import com.cep.service.domain.Passenger;
import com.cep.service.domain.Reservation;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlOperations;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.add;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

@Component
public class FlightDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightDao.class);
    private static final String FLIGHT = "flight";
    private static final String FLIGHT_NUMBER = "flightNumber";
    private static final String DEPARTURE_AIRPORT = "departureAirport";
    private static final String ARRIVAL_AIRPORT = "arrivalAirport";
    private static final String DEPARTURE_TIME = "departureTime";
    private static final String ARRIVAL_TIME = "arrivalTime";
    private static final String AIRCRAFT_TAIL_NUMBER = "aircraftTailNumber";
    private static final String FLIGHT_DATE = "flightDate";
    private static final String CARRIER_CODE = "carrierCode";
    private static final String FLIGHT_KEY = "flightKey";
    private static final String CONFIRMATION_NUMBERS = "confirmationNumbers";


    @Autowired
    private CassandraConfiguration cassandraConfiguration;

    @Autowired
    private CqlOperations cqlOperations;


    @SuppressWarnings("unchecked")
    public List<FlightEvent> findAllFlights() {
        List<FlightEvent> flightEvents = new ArrayList<>();

        String query = String.format("select * from %s.%s;", cassandraConfiguration.getKeyspaceName(), FLIGHT);
        flightEvents = cqlOperations.query(query, new FlightRowMapper());
        return flightEvents;
    }

    public FlightEvent findFlight(String flightKey) {

        Select select = QueryBuilder.select().from(cassandraConfiguration.getKeyspaceName(), FLIGHT);
        select.where(eq(FLIGHT_KEY, flightKey)).limit(1);

        List<FlightEvent> flightEvents = cqlOperations.query(select, new FlightRowMapper());

        // TODO boundary condition for non-existence of data. for the overall flow.
        return flightEvents.get(0);
    }

    /**
     * @param flightKeys     - flight keys to search.
     * @param afterFlightKey - flight key after which the flights need to be returned.
     * @return Returns all the flights this passenger is travelling. The returned flights are
     * ordered in the returned list by their departure time. The returned list drops any flight
     * that was before the provided flight key. But Includes the provided flight in the returned list.
     */
    public List<FlightEvent> findFlightsAfter(List<String> flightKeys, String afterFlightKey) {

        List<FlightEvent> flightEvents = new ArrayList<>();
        boolean start = false;
        for (FlightEvent flightEvent : findFlights(flightKeys)) {

            if (flightEvent.getFlightInfo().flightKey().equals(afterFlightKey)) {
                start = true;
            }
            if (start) {
                flightEvents.add(flightEvent);
            }
        }
        return flightEvents;

    }

    /**
     * @param flightKeys - Flights keys to search.
     * @return All flights for the provided keys, ordered by the flight departure.
     */
    public List<FlightEvent> findFlights(List<String> flightKeys) {

        List<FlightEvent> flightEvents = flightKeys.stream().map(this::findFlight).collect(Collectors.toList());
        Collections.sort(flightEvents, (a, b) -> a.getFlightInfo().getFlightDepartureTime().compareTo(b.getFlightInfo().getFlightDepartureTime()));
        return flightEvents;

    }

    public Set<String> findPnrs(String flightKey) {

        FlightEvent flightEvent = findFlight(flightKey);

        if (flightEvent != null) {
            return flightEvent.getReservation().getConfirmationNumbers();
        } else {
            LOGGER.info("Could not find flight for flight key: {}", flightKey);
            return new HashSet<>();
        }
    }

    public void save(FlightEvent flightEvent) {

        LOGGER.info("Save flightEvent: {}", flightEvent);

        Insert insertQuery = QueryBuilder.insertInto(cassandraConfiguration.getKeyspaceName(), FLIGHT)
                .value(FLIGHT_KEY, flightEvent.getFlightInfo().flightKey())
                .value(CARRIER_CODE, flightEvent.getFlightInfo().getCarrierCode())
                .value(FLIGHT_NUMBER, flightEvent.getFlightInfo().getFlightNumber())
                // .value(FLIGHT_DATE, LocalDate.fromMillisSinceEpoch(flightEvent.getFlightInfo().getFlightDate().getTime()))
                .value(FLIGHT_DATE, new Timestamp(flightEvent.getFlightInfo().getFlightDate().getTime()))
                .value(DEPARTURE_AIRPORT, flightEvent.getFlightInfo().getDepartureAirport())
                .value(ARRIVAL_AIRPORT, flightEvent.getFlightInfo().getArrivalAirport())
                .value(DEPARTURE_TIME, new Timestamp(flightEvent.getFlightInfo().getFlightDepartureTime().getTime()))
                .value(ARRIVAL_TIME, new Timestamp(flightEvent.getFlightInfo().getFlightArrivalTime().getTime()))
                .value(AIRCRAFT_TAIL_NUMBER, flightEvent.getReservation().getAircraftTailNumber());

        cqlOperations.getSession().execute(insertQuery);
        LOGGER.info("flightEvent saved {}", flightEvent);

    }

    public void updateConfirmationNumber(String flightKey, Passenger passenger) {

        LOGGER.info("Update confirmation number for flightKey: {} and passenger {}", flightKey, passenger);

        Statement updateQuery = QueryBuilder.update(cassandraConfiguration.getKeyspaceName(), FLIGHT)
                .with(add(CONFIRMATION_NUMBERS, passenger.getConfirmationNumber()))
                .where(eq(FLIGHT_KEY, flightKey));

        cqlOperations.getSession().execute(updateQuery);

        LOGGER.info("Confirmation number updated for flightKey: {} and passenger {}", flightKey, passenger);
    }

    static class FlightRowMapper implements RowMapper {


        @Override
        public FlightEvent mapRow(Row row, int i) throws DriverException {
            FlightEvent flightEvent = new FlightEvent();
            FlightInfo flightInfo = new FlightInfo();
            flightEvent.setKey(row.getString(FLIGHT_KEY));
            flightInfo.setCarrierCode(row.getString(CARRIER_CODE));
            flightInfo.setFlightNumber(row.getInt(FLIGHT_NUMBER));
            if (row.getTimestamp(FLIGHT_DATE) != null) {
                // flightInfo.setFlightDate(new Date(row.getDate(FLIGHT_DATE).getMillisSinceEpoch()));
                flightInfo.setFlightDate(row.getTimestamp(FLIGHT_DATE));
            }
            flightInfo.setDepartureAirport(row.getString(DEPARTURE_AIRPORT));
            flightInfo.setArrivalAirport(row.getString(ARRIVAL_AIRPORT));
            flightInfo.setFlightDepartureTime(row.getTimestamp(DEPARTURE_TIME));
            flightInfo.setFlightArrivalTime(row.getTimestamp(ARRIVAL_TIME));

            Reservation reservation = new Reservation();
            reservation.setAircraftTailNumber(row.getString(AIRCRAFT_TAIL_NUMBER));
            reservation.setConfirmationNumbers(row.getSet(CONFIRMATION_NUMBERS, String.class));

            flightEvent.setFlightInfo(flightInfo);
            flightEvent.setReservation(reservation);
            return flightEvent;
        }
    }
}
