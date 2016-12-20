package com.cep.service.dao;

import com.cep.service.configuration.CassandraConfiguration;
import com.cep.service.domain.Passenger;
import com.cep.service.domain.PassengerOnFlightEvent;
import com.cep.service.domain.Pnr;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlOperations;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Component
public class PassengerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassengerDao.class);
    private static final String PNR = "pnr";
    private static final String CONFIRMATION_NUMBER = "confirmationNumber";
    private static final String FLIGHT_KEYS = "flightKeys";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    @Autowired
    private CassandraConfiguration cassandraConfiguration;

    @Autowired
    private CqlOperations cqlOperations;

    public List<Pnr> findAllPnrs() {
        List<Pnr> pnrs = new ArrayList<>();

        String query = String.format("select * from %s.%s;", cassandraConfiguration.getKeyspaceName(), PNR);
        pnrs = cqlOperations.query(query, new PnrRowMapper());
        return pnrs;
    }

    public void save(PassengerOnFlightEvent passengerOnFlightEvent) {

        LOGGER.info("Save : {}", passengerOnFlightEvent);

        Statement updateQuery = QueryBuilder.update(cassandraConfiguration.getKeyspaceName(), PNR)
                .with(set(FIRST_NAME, passengerOnFlightEvent.getPassenger().getFirstName()))
                .and(set(LAST_NAME, passengerOnFlightEvent.getPassenger().getLastName()))
                .and(add(FLIGHT_KEYS, passengerOnFlightEvent.getFlightInfo().flightKey()))
                .where(eq(CONFIRMATION_NUMBER, passengerOnFlightEvent.getPassenger().getConfirmationNumber()));

        cqlOperations.getSession().execute(updateQuery);

        LOGGER.info("Event saved {}", passengerOnFlightEvent);

    }

    /**
     * @param confirmationNumber - The confirmation number for which flights need to retrieved.
     * @return Returns the all the flights this passenger is travelling.
     */
    public List<String> findFlightKeys(String confirmationNumber) {

        Select select = QueryBuilder.select(FLIGHT_KEYS).from(cassandraConfiguration.getKeyspaceName(), PNR);
        select.where(eq(CONFIRMATION_NUMBER, confirmationNumber)).limit(1);

        List<Set<String>> result = cqlOperations.query(select, new RowMapper<Set<String>>() {
            @Override
            public Set<String> mapRow(Row row, int i) throws DriverException {
                return row.getSet(FLIGHT_KEYS, String.class);
            }
        });

        // TODO cover boundary condition non existence of data.
        return new ArrayList<>(result.get(0));

    }


    static class PnrRowMapper implements RowMapper {


        @Override
        public Pnr mapRow(Row row, int i) throws DriverException {

            Pnr pnr = new Pnr();
            pnr.setFlightKeys(row.getSet(FLIGHT_KEYS, String.class));

            Passenger passenger = new Passenger();
            passenger.setConfirmationNumber(row.getString(CONFIRMATION_NUMBER));
            passenger.setFirstName(row.getString(FIRST_NAME));
            passenger.setLastName(row.getString(LAST_NAME));

            pnr.setPassenger(passenger);

            return pnr;
        }
    }
}
