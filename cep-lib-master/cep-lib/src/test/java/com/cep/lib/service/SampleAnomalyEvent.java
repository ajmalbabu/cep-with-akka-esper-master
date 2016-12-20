package com.cep.lib.service;


import com.cep.lib.domain.DefaultCepMessage;

import java.io.Serializable;

public class SampleAnomalyEvent extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public SampleAnomalyEvent(Object shardId, String entityId) {
        super(shardId, entityId);
    }

    @Override
    public String toString() {
        return "SampleAnomalyEvent{id: " +
                super.toString() +
                "}";
    }
}
