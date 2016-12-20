package com.cep.service.domain;

import java.io.Serializable;
import java.util.List;

/**
 * A useful extension for cep message that can hold a parametrized affectedPnrs.
 */
public class AffectedPnr implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> affectedPnrs;
    private String responsibleFlightKey;

    public AffectedPnr(List<String> affectedPnrs, String responsibleFlightKey) {
        this.affectedPnrs = affectedPnrs;
        this.responsibleFlightKey = responsibleFlightKey;
    }

    public List<String> getAffectedPnrs() {
        return affectedPnrs;
    }

    public boolean hasPnrs() {
        return affectedPnrs != null && affectedPnrs.size() > 0;
    }

    @Override
    public String toString() {
        return "AffectedPnr{" +
                "affectedPnrs=" + affectedPnrs +
                ", responsibleFlightKey=" + responsibleFlightKey +
                '}';
    }

}
