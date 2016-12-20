package com.cep.lib.service;

import com.cep.lib.domain.Anomaly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Publishes anomaly to log stream.
 */
@Service("consoleAnomalyPublisher")
public class ConsoleAnomalyPublisher implements AnomalyPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleAnomalyPublisher.class);

    @Override
    public void publish(Anomaly anomaly) {
        LOGGER.info("\n\r ************** \n\r Publishing ANOMALY \n\r ************** \n\r\n\r: " +
                "{} \n\r\n\r **************", anomaly);
    }
}
