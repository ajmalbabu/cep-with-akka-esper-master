package com.cep.service.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FlightEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String eventType;
    private FlightInfo flightInfo;
    private Reservation reservation;

    public FlightEvent() {
    }

    public FlightEvent(FlightInfo flightInfo, Reservation reservation) {
        this.flightInfo = flightInfo;
        this.reservation = reservation;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String flightKey() {
        return flightInfo.flightKey();
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public FlightInfo getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(FlightInfo flightInfo) {
        this.flightInfo = flightInfo;
    }

    @Override
    public String toString() {
        return "FlightEvent{" +
                "flightInfo='" + flightInfo + '\'' +
                ", reservation='" + reservation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightEvent that = (FlightEvent) o;

        if (flightInfo != null ? !flightInfo.equals(that.flightInfo) : that.flightInfo != null) return false;
        return reservation != null ? reservation.equals(that.reservation) : that.reservation == null;

    }

    @Override
    public int hashCode() {
        int result = flightInfo != null ? flightInfo.hashCode() : 0;
        result = 31 * result + (reservation != null ? reservation.hashCode() : 0);
        return result;
    }

    public static FlightEvent createFlightEvent(int flightNumber, String origin,
                                                String destination, String flightDate,
                                                String departureTime, String arrivalTime) throws ParseException {

        FlightEvent flightEvent = new FlightEvent();
        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(flightNumber);
        flightInfo.setFlightDate(new SimpleDateFormat("yyyy-mm-dd").parse(flightDate));
        flightInfo.setFlightDepartureTime(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(departureTime));
        flightInfo.setFlightArrivalTime(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(arrivalTime));
        flightInfo.setCarrierCode("WN");
        flightInfo.setDepartureAirport(origin);
        flightInfo.setArrivalAirport(destination);
        flightEvent.setFlightInfo(flightInfo);
        return flightEvent;
    }

}
