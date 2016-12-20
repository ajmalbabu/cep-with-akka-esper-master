package com.cep.service.anomaly.esper1;

import com.cep.lib.domain.Anomaly;
import com.cep.lib.service.AnomalyPublisher;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrequentFlightDelayAnomalyListener implements UpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrequentFlightDelayAnomalyListener.class);
    private AnomalyPublisher anomalyPublisher;

    public FrequentFlightDelayAnomalyListener(AnomalyPublisher anomalyPublisher) {

        this.anomalyPublisher = anomalyPublisher;
    }

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {

        LOGGER.info("Found frequent flight anomaly with new event & length is: {}", newEvents.length);

        if (newEvents.length > 0) {

            Anomaly<String> anomaly = new Anomaly<>("Frequent flight delay Count: " + newEvents[0].get("cnt").toString());
            anomalyPublisher.publish(anomaly);
        }

    }

}