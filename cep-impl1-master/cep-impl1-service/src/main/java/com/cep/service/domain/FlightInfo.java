package com.cep.service.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String carrierCode;
    private int flightNumber;
    private Date flightDate;
    private String departureAirport;
    private String arrivalAirport;
    private Date flightDepartureTime;
    private Date flightArrivalTime;


    public FlightInfo() {
    }


    public int getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String flightKey() {
        return carrierCode + "-" + flightNumber + "-"
                + ((flightDate == null) ? "null" : simpleDateFormat.format(flightDate))
                + "-" + departureAirport + "-" + arrivalAirport;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Date getFlightDepartureTime() {
        return flightDepartureTime;
    }

    public void setFlightDepartureTime(Date flightDepartureTime) {
        this.flightDepartureTime = flightDepartureTime;
    }

    public Date getFlightArrivalTime() {
        return flightArrivalTime;
    }

    public void setFlightArrivalTime(Date flightArrivalTime) {
        this.flightArrivalTime = flightArrivalTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightInfo flightInfo = (FlightInfo) o;

        if (flightNumber != flightInfo.flightNumber) return false;
        if (carrierCode != null ? !carrierCode.equals(flightInfo.carrierCode) : flightInfo.carrierCode != null)
            return false;
        if (flightDate != null ? !flightDate.equals(flightInfo.flightDate) : flightInfo.flightDate != null)
            return false;
        if (departureAirport != null ? !departureAirport.equals(flightInfo.departureAirport) : flightInfo.departureAirport != null)
            return false;
        if (arrivalAirport != null ? !arrivalAirport.equals(flightInfo.arrivalAirport) : flightInfo.arrivalAirport != null)
            return false;
        if (flightDepartureTime != null ? !flightDepartureTime.equals(flightInfo.flightDepartureTime) : flightInfo.flightDepartureTime != null)
            return false;
        return flightArrivalTime != null ? flightArrivalTime.equals(flightInfo.flightArrivalTime) : flightInfo.flightArrivalTime == null;

    }

    @Override
    public int hashCode() {
        int result = carrierCode != null ? carrierCode.hashCode() : 0;
        result = 31 * result + flightNumber;
        result = 31 * result + (flightDate != null ? flightDate.hashCode() : 0);
        result = 31 * result + (departureAirport != null ? departureAirport.hashCode() : 0);
        result = 31 * result + (arrivalAirport != null ? arrivalAirport.hashCode() : 0);
        result = 31 * result + (flightDepartureTime != null ? flightDepartureTime.hashCode() : 0);
        result = 31 * result + (flightArrivalTime != null ? flightArrivalTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FlightInfo{" +
                "carrierCode='" + carrierCode + '\'' +
                ", flightNumber=" + flightNumber +
                ", flightDate=" + flightDate +
                ", departureAirport='" + departureAirport + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                ", flightDepartureTime=" + flightDepartureTime +
                ", flightArrivalTime=" + flightArrivalTime +
                '}';
    }
}
