package com.cep.lib.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnomalyTest {


    @Test
    public void anomalyTest() throws Exception {
        Anomaly<String> anomaly = new Anomaly<>("Test Anomaly");
        assertThat(anomaly.getMessage()).isEqualTo("Test Anomaly");
        assertThat(anomaly.toString()).contains("Test Anomaly");
        assertThat(anomaly.hashCode()).isEqualTo(new Anomaly<>("Test Anomaly").hashCode());
        assertThat(anomaly.equals(new Anomaly<>("Test Anomaly"))).isEqualTo(true);

    }
}
