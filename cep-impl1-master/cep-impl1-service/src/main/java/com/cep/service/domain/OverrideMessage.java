package com.cep.service.domain;

import com.cep.lib.domain.DefaultCepMessage;

import java.io.Serializable;

public class OverrideMessage extends DefaultCepMessage implements Serializable {
    private final FlightInfo flightInfo;

    public OverrideMessage(Object shardId, FlightInfo flightInfo) {
        super(shardId, flightInfo.flightKey());
        this.flightInfo = flightInfo;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof OverrideMessage)) return false;

        OverrideMessage that = (OverrideMessage) o;

        return flightInfo != null ? flightInfo.equals(that.flightInfo) : that.flightInfo == null;

    }

    @Override
    public int hashCode() {
        return flightInfo != null ? flightInfo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "OverrideMessage{" +
                "flightInfo=" + flightInfo +
                '}';
    }
}
