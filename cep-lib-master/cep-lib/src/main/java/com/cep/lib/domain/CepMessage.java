package com.cep.lib.domain;

import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;

public interface CepMessage {

    void setSequenceNr(long sequenceNr);

    long getSequenceNr();

    Object getShardId();

    String getEntityId();

    Map<String, Object> getMdc();

    Instant getCreateTime();

    default boolean isAfter(Instant now) {
        return getCreateTime().isAfter(now);
    }

    default boolean isBefore(Instant now) {
        return getCreateTime().isBefore(now);
    }

    /**
     * If the Create-Time is before (now - provided timeWindow)
     */
    default boolean isBefore(Time timeWindow) {
        return isBefore(currentTime().minusMillis(timeWindow.toMillis()));
    }

    default Instant currentTime() {
        return Instant.now();
    }

    static Consumer<CepMessage> noFunction() {
        return null;
    }
}

