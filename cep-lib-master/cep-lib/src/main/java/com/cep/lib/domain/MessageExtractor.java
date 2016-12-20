package com.cep.lib.domain;

import akka.cluster.sharding.ShardRegion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageExtractor implements ShardRegion.MessageExtractor {

    @Value("${cep.akka.number.of.shards:10}")
    private int maxNumberOfShards;

    @Override
    public String shardId(Object message) {

        if (message instanceof CepMessage) {
            return String.valueOf(Math.abs(((CepMessage) message).getShardId().hashCode()) % maxNumberOfShards);
        } else {
            return handleError(message);
        }
    }

    @Override
    public String entityId(Object message) {
        if (message instanceof CepMessage)
            return ((CepMessage) message).getEntityId();
        else
            return handleError(message);
    }

    @Override
    public Object entityMessage(Object message) {
        if (message instanceof CepMessage)
            return message;
        else
            return handleError(message);
    }

    private String handleError(Object message) {
        throw new IllegalStateException("Wrong message type: " + message);
    }

    public void setMaxNumberOfShards(int maxNumberOfShards) {
        this.maxNumberOfShards = maxNumberOfShards;
    }
}
