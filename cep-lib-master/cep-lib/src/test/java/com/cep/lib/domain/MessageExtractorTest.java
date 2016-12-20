package com.cep.lib.domain;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MessageExtractorTest {

    @Test
    public void normalTest() throws Exception {
        MessageExtractor messageExtractor = new MessageExtractor();
        messageExtractor.setMaxNumberOfShards(10);
        assertThat(messageExtractor.shardId(new HeartBeatMessage("1", "2"))).isEqualTo("9");
        assertThat(messageExtractor.shardId(new HeartBeatMessage("8", "2"))).isEqualTo("6");
        assertThat(messageExtractor.entityId(new HeartBeatMessage("1", "2"))).isEqualTo("2");
        assertThat(messageExtractor.entityMessage(new HeartBeatMessage("1", "2")).toString()).contains("shardId='1', entityId='2'");
    }

    @Test
    public void exceptionTest() throws Exception {
        MessageExtractor messageExtractor = new MessageExtractor();
        assertThatThrownBy(() -> messageExtractor.shardId("test")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> messageExtractor.entityId("test")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> messageExtractor.entityMessage("test")).isInstanceOf(IllegalStateException.class);
    }
}
