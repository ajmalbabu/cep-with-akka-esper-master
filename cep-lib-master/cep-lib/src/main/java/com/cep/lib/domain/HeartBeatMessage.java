package com.cep.lib.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public class HeartBeatMessage extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public HeartBeatMessage(Object shardId, String entityId) {
        this(shardId, entityId, transactionId.currentTransactionIdAsMap(), Instant.now());
    }


    public HeartBeatMessage(Object shardId, String entityId, Map<String, Object> mdc, Instant instant) {
        super(shardId, entityId, mdc, instant);
    }

    @Override
    public String toString() {
        return "HeartBeatMessage{" +
                super.toString() +
                "}";
    }
}
