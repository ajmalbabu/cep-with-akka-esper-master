package com.cep.service.dataservice;


import com.cep.service.dao.FlightDao;
import com.cep.service.domain.FlightEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightService {

    @Autowired
    private FlightDao flightDao;

    public List<FlightEvent> findAllFlights() {
        return flightDao.findAllFlights();
    }
}
