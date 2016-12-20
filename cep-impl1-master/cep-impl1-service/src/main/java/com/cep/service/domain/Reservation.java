package com.cep.service.domain;


import java.io.Serializable;
import java.util.Set;

public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<String> confirmationNumbers;
    // Move this to aircraft object.
    private String aircraftTailNumber;


    public Reservation() {
    }


    public Set<String> getConfirmationNumbers() {
        return confirmationNumbers;
    }

    public void setConfirmationNumbers(Set<String> confirmationNumbers) {
        this.confirmationNumbers = confirmationNumbers;
    }

    public String getAircraftTailNumber() {
        return aircraftTailNumber;
    }

    public void setAircraftTailNumber(String aircraftTailNumber) {
        this.aircraftTailNumber = aircraftTailNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation reservation = (Reservation) o;

        if (confirmationNumbers != null ? !confirmationNumbers.equals(reservation.confirmationNumbers) : reservation.confirmationNumbers != null)
            return false;
        return aircraftTailNumber != null ? aircraftTailNumber.equals(reservation.aircraftTailNumber) : reservation.aircraftTailNumber == null;

    }

    @Override
    public int hashCode() {
        int result = confirmationNumbers != null ? confirmationNumbers.hashCode() : 0;
        result = 31 * result + (aircraftTailNumber != null ? aircraftTailNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "confirmationNumbers=" + confirmationNumbers +
                ", aircraftTailNumber='" + aircraftTailNumber + '\'' +
                '}';
    }
}
