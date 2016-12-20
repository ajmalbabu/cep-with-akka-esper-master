package com.cep.lib.service;


import com.cep.lib.domain.DefaultCepMessage;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public class UnknownMessage extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public UnknownMessage(Object shardId, String entityId) {
        super(shardId, entityId);
    }


    @Override
    public String toString() {
        return "UnknownMessage{" +
                super.toString() +
                '}';
    }
}
