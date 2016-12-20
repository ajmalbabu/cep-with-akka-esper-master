package com.cep.service.domain;

import com.cep.lib.domain.DefaultCepMessage;

import java.io.Serializable;

public class FlightDelayMessage extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private FlightInfo flightInfo;


    public FlightDelayMessage(Object shardId, FlightInfo flightInfo) {
        super(shardId, flightInfo.flightKey());
        this.flightInfo = flightInfo;
    }

    public FlightInfo getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(FlightInfo flightInfo) {
        this.flightInfo = flightInfo;
    }

    public Integer getFlightNumber() {
        return flightInfo.getFlightNumber();
    }

    public static FlightDelayMessage createFlightDelayForPaxImpactDetection(FlightEvent flightEvent) {
        return new FlightDelayMessage(flightEvent.getFlightInfo().flightKey(), flightEvent.getFlightInfo());
    }

    public static FlightDelayMessage createFlightDelayForFrequentDelayDetection(FlightEvent flightEvent) {
        return new FlightDelayMessage(flightEvent.getFlightInfo().flightKey(), flightEvent.getFlightInfo());
    }

    public boolean after(FlightDelayMessage flightDelayMessage) {
        return this.getFlightInfo().getFlightDepartureTime().after(flightDelayMessage.getFlightInfo().getFlightDepartureTime());
    }

    @Override
    public String toString() {
        return "FlightDelayMessage{" +
                super.toString() +
                "flightInfo=" + flightInfo +
                '}';
    }
}
