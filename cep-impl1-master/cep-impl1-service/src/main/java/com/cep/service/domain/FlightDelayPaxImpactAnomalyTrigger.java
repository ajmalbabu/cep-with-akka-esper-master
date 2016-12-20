package com.cep.service.domain;


import com.cep.lib.domain.DefaultCepMessage;

import java.io.Serializable;

/**
 * A CEP message that can trigger an anomaly.
 */
public class FlightDelayPaxImpactAnomalyTrigger extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id of the Flight delay induced message that caused this anomaly trigger.
     */
    private String responsibleFlightDelayInducedFlightKey;


    public FlightDelayPaxImpactAnomalyTrigger(Object shardId, String entityId, String responsibleFlightDelayInducedFlightKey) {
        super(shardId, entityId);
        this.responsibleFlightDelayInducedFlightKey = responsibleFlightDelayInducedFlightKey;
    }


    public String getResponsibleFlightDelayInducedFlightKey() {
        return responsibleFlightDelayInducedFlightKey;
    }

    public void setResponsibleFlightDelayInducedFlightKey(String responsibleFlightDelayInducedFlightKey) {
        this.responsibleFlightDelayInducedFlightKey = responsibleFlightDelayInducedFlightKey;
    }

    @Override
    public String toString() {
        return "FlightDelayPaxImpactAnomalyTrigger{" +
                super.toString() +
                "responsibleFlightDelayInducedFlightKey=" + responsibleFlightDelayInducedFlightKey +
                '}';
    }
}
