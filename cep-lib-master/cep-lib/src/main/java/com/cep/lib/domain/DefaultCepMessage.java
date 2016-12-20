package com.cep.lib.domain;


import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * A convenient implementation of CepMessage interface.
 */
public class DefaultCepMessage implements CepMessage, Serializable {

    private static final long serialVersionUID = 1L;
    public static final String SELF_MESSAGE = "SelfMessage";

    protected static TransactionId transactionId = TransactionId.instance();
    private Object shardId;
    private String entityId;
    private Instant createTime;
    private Map<String, Object> mdc;
    private long sequenceNr;

    public DefaultCepMessage(Object shardId, String entityId) {
        this(shardId, entityId, transactionId.currentTransactionIdAsMap(), Instant.now());
    }

    public DefaultCepMessage(Object shardId, String entityId, Map<String, Object> mdc, Instant createTime) {
        this.shardId = shardId;
        this.entityId = entityId;
        this.mdc = mdc;
        this.createTime = createTime;
    }

    @Override
    public Map<String, Object> getMdc() {
        return mdc;
    }


    @Override
    public Instant getCreateTime() {
        return createTime;
    }


    @Override
    public String getEntityId() {
        return entityId;
    }


    @Override
    public void setSequenceNr(long sequenceNr) {

        this.sequenceNr = sequenceNr;
    }

    @Override
    public long getSequenceNr() {
        return sequenceNr;
    }

    public Object getShardId() {
        return shardId;
    }


    @Override
    public String toString() {
        return "DefaultCepMessage{" +
                "mdc=" + mdc +
                ", shardId='" + shardId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", createTime=" + createTime +
                '}';
    }


}

