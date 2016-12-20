package com.cep.service.domain;


import com.cep.lib.domain.DefaultCepMessage;

import java.io.Serializable;

/**
 * The message that is generated as a result of the flight delay event.
 */
public class FlightDelayInducedMessage extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private FlightDelayMessage responsibleFlightDelayMessage;
    private EventType resultingEventType;
    private AffectedPnr affectedPnr;

    public FlightDelayInducedMessage(FlightDelayMessage responsibleFlightDelayMessage,
                                     EventType resultingEventType, AffectedPnr affectedPnr) {

        super(responsibleFlightDelayMessage.getShardId(), responsibleFlightDelayMessage.getEntityId());
        this.responsibleFlightDelayMessage = responsibleFlightDelayMessage;
        this.resultingEventType = resultingEventType;
        this.affectedPnr = affectedPnr;
    }

    public FlightDelayMessage getResponsibleFlightDelayMessage() {
        return responsibleFlightDelayMessage;
    }

    public void setResponsibleFlightDelayMessage(FlightDelayMessage responsibleFlightDelayMessage) {
        this.responsibleFlightDelayMessage = responsibleFlightDelayMessage;
    }

    public EventType getResultingEventType() {
        return resultingEventType;
    }

    public void setResultingEventType(EventType resultingEventType) {
        this.resultingEventType = resultingEventType;
    }

    public AffectedPnr getAffectedPnr() {
        return affectedPnr;
    }

    public void setAffectedPnr(AffectedPnr affectedPnr) {
        this.affectedPnr = affectedPnr;
    }

    public boolean hasAffectedPnrs() {
        return affectedPnr != null && affectedPnr.hasPnrs();
    }


    @Override
    public String toString() {
        return "FlightDelayInducedMessage{" +
                super.toString() +
                ", responsibleFlightDelayMessage=" + responsibleFlightDelayMessage +
                ", resultingEventType=" + resultingEventType +
                ", affectedPnr=" + affectedPnr +
                '}';
    }
}
