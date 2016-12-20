package com.cep.service.anomaly.esper1;


import com.cep.lib.domain.*;
import com.cep.lib.service.AnomalyPublisher;
import com.cep.lib.service.CepPersistenceActor;
import com.cep.service.domain.FlightDelayMessage;
import com.cep.service.domain.FreqFlightDelayState;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

public class FrequentFlightDelayAnomalyDetector extends CepPersistenceActor implements TimeToLive, MessageExpiry {

    private static final String ANOMALY_PUBLISHER_BEAN = "anomaly.publisher.bean";
    private static final String FREQUENT_FLIGHT_DELAY_ANOMALY_CHECK_WINDOW_MILLIE_SECONDS = "frequent.flight.delay.anomaly.check.window.millis";
    private static final String FREQUENT_FLIGHT_DELAY_COUNT_THRESHOLD_FOR_ANOMALY_GENERATION = "frequent.flight.delay.count.threshold.for.anomaly.generation";

    private AnomalyPublisher anomalyPublisher;
    private long freqFlightDelayAnomalyCheckWindow;
    private int freqFlightDelayAnomalyThresholdCount;
    private FrequentFlightDelayEsperEngine frequentFlightDelayEsperEngine;
    private Time actorTTL;
    private Time messageExpiry;

    private FreqFlightDelayState state = new FreqFlightDelayState();


    public FrequentFlightDelayAnomalyDetector(ApplicationContext applicationContext, Parameters parameters) {

        anomalyPublisher = applicationContext.getBean(parameters.getString(ANOMALY_PUBLISHER_BEAN), AnomalyPublisher.class);
        freqFlightDelayAnomalyCheckWindow = parameters.parseLong(FREQUENT_FLIGHT_DELAY_ANOMALY_CHECK_WINDOW_MILLIE_SECONDS);
        freqFlightDelayAnomalyThresholdCount = parameters.parseInt(FREQUENT_FLIGHT_DELAY_COUNT_THRESHOLD_FOR_ANOMALY_GENERATION);
        frequentFlightDelayEsperEngine = new FrequentFlightDelayEsperEngine(anomalyPublisher,
                freqFlightDelayAnomalyCheckWindow, freqFlightDelayAnomalyThresholdCount);

        actorTTL = new Time(parameters.parseLong(ACTOR_TIME_TO_LIVE_MINUTES), TimeUnit.MINUTES);
        messageExpiry = new Time(parameters.parseLong(ACTOR_STATE_MESSAGE_EXPIRY_TIME_MINUTES), TimeUnit.MINUTES);


    }

    @Override
    public String persistenceId() {
        return getSelf().path().parent() + "-" + getSelf().path().name();
    }

    @Override
    public void handleReceiveRecover(CepMessage cepMessage) {

        LOGGER.info("Receive recover {} state: {}", this, state.toString());

        if (cepMessage instanceof FlightDelayMessage) {

            sendEvent((FlightDelayMessage) cepMessage);

        }
    }


    @Override
    public void handleReceiveCommand(CepMessage cepMessage) {

        LOGGER.info("Receive command {} state: {}", this, state.toString());

        if (cepMessage instanceof FlightDelayMessage) {

            store(cepMessage, this::sendEvent);

        }

        acknowledge();
    }

    private void sendEvent(FlightDelayMessage flightDelayMessage) {
        frequentFlightDelayEsperEngine.sendEvent(flightDelayMessage);
    }

    @Override
    public MessageExpiryListener messageExpiryListener() {
        return state;
    }

    @Override
    public Time expiryTime() {
        return messageExpiry;
    }

    @Override
    public Time actorTtl() {
        return actorTTL;
    }
}