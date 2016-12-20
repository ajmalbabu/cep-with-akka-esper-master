package com.cep.service.domain;

import java.io.Serializable;

public class Passenger implements Serializable {

    private static final long serialVersionUID = 1L;
    private String confirmationNumber;
    private String firstName;
    private String lastName;

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Passenger passenger = (Passenger) o;

        return confirmationNumber.equals(passenger.confirmationNumber);

    }

    @Override
    public int hashCode() {
        return confirmationNumber.hashCode();
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "confirmationNumber='" + confirmationNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
