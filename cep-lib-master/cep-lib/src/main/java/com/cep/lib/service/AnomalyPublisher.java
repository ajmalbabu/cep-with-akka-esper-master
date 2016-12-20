package com.cep.lib.service;

import com.cep.lib.domain.Anomaly;

/**
 * Publish anomaly into a specific destination.
 */
public interface AnomalyPublisher {

    void publish(Anomaly anomaly);
}
