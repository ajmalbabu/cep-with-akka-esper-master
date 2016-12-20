package com.cep.service.domain;


import com.cep.lib.domain.MessageExpiryListener;
import com.cep.lib.domain.Time;

import java.util.ArrayList;
import java.util.List;


public class FreqFlightDelayState implements MessageExpiryListener {

    private List<FlightDelayMessage> flightDelayMessages = new ArrayList<>();

    public void addFlightDelayMessage(FlightDelayMessage flightDelayMessage) {
        flightDelayMessages.add(flightDelayMessage);
    }


    @Override
    public String toString() {
        return "FreqFlightDelayState{" +
                "flightDelayMessages=" + flightDelayMessages +
                '}';
    }

    @Override
    public long expirySequenceNr(Time timeWindow) {
        return 0;
    }

    @Override
    public void cleanupState(long toSequenceNr) {

    }
}

