package com.cep.lib.service;

import com.cep.lib.domain.Anomaly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Can listen for anomalies, helpful during unit testing to perform asserts.
 */
@Service("observableAnomalyPublisher")
public class ObservableAnomalyPublisher extends Observable implements AnomalyPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservableAnomalyPublisher.class);

    private AnomalyObserver anomalyObserver = new AnomalyObserver();

    @Override
    public void publish(Anomaly anomaly) {
        setChanged();
        notifyObservers(anomaly);
    }

    @PostConstruct
    public void postConstruct() {
        this.addObserver(anomalyObserver);
    }

    public AnomalyObserver getAnomalyObserver() {
        return anomalyObserver;
    }

    public List<Anomaly> blockForAnomalies(long sleepIntervalMillis, long maxSleepMillis) {
        return getAnomalyObserver().blockForAnomalies(sleepIntervalMillis, maxSleepMillis);
    }

    public void clear() {
        getAnomalyObserver().clear();
    }

    public static class AnomalyObserver implements Observer {

        private List<Anomaly> anomalies = new ArrayList<>();

        @Override
        public void update(Observable o, Object arg) {

            anomalies.add((Anomaly) arg);
        }

        public void clear() {
            anomalies.clear();
        }

        public List<Anomaly> blockForAnomalies(long sleepIntervalMillis, long maxSleepMillis) {
            Instant startTime = Instant.now();
            while (anomalies.size() == 0 && Instant.now().minusMillis(maxSleepMillis).isBefore(startTime)) {
                try {
                    Thread.sleep(sleepIntervalMillis);
                } catch (InterruptedException e) {
                    LOGGER.warn("Try again, error processing request: {}", e.toString());
                }
            }

            return anomalies;
        }
    }

}
