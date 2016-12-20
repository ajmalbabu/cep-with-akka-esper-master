package com.cep.service.domain;


import java.io.Serializable;

public class PassengerOnFlightEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private FlightInfo flightInfo;
    private Passenger passenger;


    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public FlightInfo getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(FlightInfo flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String confirmationNumber() {
        return passenger.getConfirmationNumber();
    }


    @Override
    public String toString() {
        return "PassengerOnFlightEvent{" +
                "flightInfo=" + flightInfo +
                ", passenger=" + passenger +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerOnFlightEvent passengerOnFlightEvent = (PassengerOnFlightEvent) o;

        return passenger != null ? passenger.equals(passengerOnFlightEvent.passenger) : passengerOnFlightEvent.passenger == null;

    }

    @Override
    public int hashCode() {
        return passenger != null ? passenger.hashCode() : 0;
    }

}
