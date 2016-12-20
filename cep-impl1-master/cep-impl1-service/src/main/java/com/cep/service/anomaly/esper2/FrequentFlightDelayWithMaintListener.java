package com.cep.service.anomaly.esper2;

import com.cep.lib.domain.Anomaly;
import com.cep.lib.service.AnomalyPublisher;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrequentFlightDelayWithMaintListener implements UpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrequentFlightDelayWithMaintListener.class);
    private AnomalyPublisher anomalyPublisher;

    public FrequentFlightDelayWithMaintListener(AnomalyPublisher anomalyPublisher) {

        this.anomalyPublisher = anomalyPublisher;
    }

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {

        LOGGER.info("Found flight delay with maint, event & length is: {}", newEvents.length);

        if (newEvents.length > 0) {

            Anomaly<String> anomaly = new Anomaly<>("Found flight delay with MAINT. first flight #: " + newEvents[0].get("cnt").toString());
            anomalyPublisher.publish(anomaly);
        }

    }

}