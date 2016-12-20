package com.cep.service.domain;


import com.cep.lib.domain.MessageExpiryListener;
import com.cep.lib.domain.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cep.service.domain.EventType.Type.FIRST_TIME;
import static com.cep.service.domain.EventType.Type.REPEATED;


public class FlightDelayPaxImpactState implements MessageExpiryListener {

    private List<FlightDelayInducedMessage> flightDelayInducedMessages = new ArrayList<>();
    private FlightDelayPaxImpactAnomalyTrigger flightDelayPaxImpactAnomalyTrigger;


    public void addFlightDelayInducedEvent(FlightDelayInducedMessage flightDelayInducedMessage) {
        flightDelayInducedMessages.add(flightDelayInducedMessage);
    }


    public void setFlightDelayPaxImpactAnomalyTrigger(FlightDelayPaxImpactAnomalyTrigger flightDelayPaxImpactAnomalyTrigger) {
        this.flightDelayPaxImpactAnomalyTrigger = flightDelayPaxImpactAnomalyTrigger;
    }

    public EventType findFlightDelayEventType(FlightDelayMessage flightDelayMessageArg) {

        EventType eventType = new EventType(FIRST_TIME);

        Optional<FlightDelayMessage> flightDelayCepMessage = lastFlightDelayMessage();
        if (flightDelayCepMessage.isPresent()) {
            if (flightDelayMessageArg.after(flightDelayCepMessage.get())) {
                eventType = new EventType(REPEATED, EventType.Direction.INCREASE);
            } else {
                eventType = new EventType(REPEATED, EventType.Direction.DECREASE);
            }
        }

        return eventType;
    }

    public Optional<FlightDelayMessage> lastFlightDelayMessage() {
        Optional<FlightDelayInducedMessage> flightDelayInducedMessage = lastFlightDelayInducedMessage();
        if (flightDelayInducedMessage.isPresent()) {
            return Optional.of(flightDelayInducedMessage.get().getResponsibleFlightDelayMessage());
        } else {
            return Optional.empty();
        }
    }

    public Optional<FlightDelayInducedMessage> lastFlightDelayInducedMessage() {
        if (flightDelayInducedMessages.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(flightDelayInducedMessages.get(flightDelayInducedMessages.size() - 1));
        }
    }

    public Optional<FlightDelayInducedMessage> findFlightDelayInducedMessageAt(String responsibleFlightInducedFlightKey) {
        for (FlightDelayInducedMessage flightDelayInducedMessage : flightDelayInducedMessages) {
            if (flightDelayInducedMessage.getResponsibleFlightDelayMessage().getFlightInfo().flightKey().equals(responsibleFlightInducedFlightKey)) {
                return Optional.of(flightDelayInducedMessage);
            }
        }

        return Optional.empty();
    }


    public boolean hasReceivedNewerFlightDelayMessageBeyond(String responsibleFlightDelayInducedFlightKey) {

        boolean foundElement = false;

        for (FlightDelayInducedMessage flightDelayInducedMessage : flightDelayInducedMessages) {
            if (foundElement) {
                return true;
            }
            if (flightDelayInducedMessage.getResponsibleFlightDelayMessage().getFlightInfo().flightKey().equals(responsibleFlightDelayInducedFlightKey)) {
                foundElement = true;
            }
        }
        if (!foundElement) {
            throw new IllegalStateException("Could not find FlightDelayInducedMessage for id" + responsibleFlightDelayInducedFlightKey
                    + ". Searched was performed inside: " + flightDelayInducedMessages);
        }
        return false;

    }

    @Override
    public String toString() {
        return "FlightDelayPaxImpactState{" +
                "flightDelayInducedMessages=" + flightDelayInducedMessages +
                ", flightDelayPaxImpactAnomalyTrigger=" + flightDelayPaxImpactAnomalyTrigger +
                '}';
    }

    @Override
    public long expirySequenceNr(Time timeWindow) {
        return 0;
    }

    @Override
    public void cleanupState(long toSequenceNr) {

    }
}

