package com.cep.service.dataservice;

import com.cep.service.common.SpringConfig;
import com.cep.service.dao.FlightDao;
import com.cep.service.domain.FlightEvent;
import com.cep.service.domain.FlightInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * This test shows how to access Cassandra layer using Mockito. cassandra-unit-test library
 * is not an active project any more, so mock all Dao layer code with Mockito.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class FlightServiceTest {


    @Mock
    private FlightDao flightDao;

    @InjectMocks
    @Autowired
    private FlightService flightService;


    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(100);
        flightInfo.setFlightDate(new SimpleDateFormat("yyyy-mm-dd").parse("2011-01-18"));
        flightInfo.setCarrierCode("WN");
        flightInfo.setDepartureAirport("MSP");
        flightInfo.setArrivalAirport("DAL");
        FlightEvent flightEvent = new FlightEvent();
        flightEvent.setFlightInfo(flightInfo);

        when(flightDao.findAllFlights()).thenReturn(asList(flightEvent));
    }


    @Test
    public void findAllFlights() throws Exception {

        // Given, When
        List<FlightEvent> flightEvents = flightService.findAllFlights();

        // Then
        assertThat(flightEvents.size()).isEqualTo(1);
        assertThat(flightEvents.get(0).flightKey()).isEqualTo("WN-100-2011-01-18-MSP-DAL");
    }


}
