package com.cep.service.domain;


import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class Pnr implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * All the flights passenger flying on.
     */
    private Set<String> flightKeys = new TreeSet<>();

    /**
     * Passenger details.
     */
    private Passenger passenger;


    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Set<String> getFlightKeys() {
        return flightKeys;
    }

    public void setFlightKeys(Set<String> flightKeys) {
        this.flightKeys = flightKeys;
    }

    public String confirmationNumber() {
        return passenger.getConfirmationNumber();
    }


    @Override
    public String toString() {
        return "PassengerOnFlightEvent{" +
                "flightKeys=" + flightKeys +
                ", passenger=" + passenger +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pnr pnr = (Pnr) o;

        return passenger != null ? passenger.equals(pnr.passenger) : pnr.passenger == null;

    }

    @Override
    public int hashCode() {
        return passenger != null ? passenger.hashCode() : 0;
    }

}
