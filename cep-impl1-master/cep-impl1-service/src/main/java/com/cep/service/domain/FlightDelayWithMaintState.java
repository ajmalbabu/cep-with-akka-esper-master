package com.cep.service.domain;


import com.cep.lib.domain.MessageExpiryListener;
import com.cep.lib.domain.Time;

import java.util.ArrayList;
import java.util.List;


public class FlightDelayWithMaintState  {

    private List<FlightDelayMessage> flightDelayMessages = new ArrayList<>();
    private List<MaintenanceMessage> maintenanceMessagesMessages = new ArrayList<>();
    private List<OverrideMessage> overrideMessages = new ArrayList<>();

    public void addFlightDelayMessage(FlightDelayMessage flightDelayMessage) {
        flightDelayMessages.add(flightDelayMessage);
    }

    public void addMaintenanceMessage(MaintenanceMessage maintenanceMessage) {
        maintenanceMessagesMessages.add(maintenanceMessage);
    }

    public void addOverrideMessage(OverrideMessage overrideMessage) {
        overrideMessages.add(overrideMessage);
    }


    @Override
    public String toString() {
        return "FlightDelayWithMaintState{" +
                " flightDelayMessages.size= " + flightDelayMessages.size() +
                ", maintenanceMessagesMessages.size= " + maintenanceMessagesMessages.size() +
                ", overrideMessages.size= " + overrideMessages.size() +
                '}';
    }
}

