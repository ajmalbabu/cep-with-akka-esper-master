package com.cep.service.anomaly.esper1;

import com.cep.lib.service.AnomalyPublisher;
import com.cep.service.domain.FlightDelayMessage;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;


public class FrequentFlightDelayEsperEngine {

    public static final String FLIGHT_DELAY_MESSAGE = "FlightDelayMessage";
    public static final String FLIGHT_DELAY_MESSAGE_ENGINE_URI = "FlightDelayEngineUri";

    private AnomalyPublisher anomalyPublisher;
    private long freqFlightDelayAnomalyCheckWindow;
    private int freqFlightDelayAnomalyThresholdCount;
    private EPServiceProvider epServiceProvider;

    public FrequentFlightDelayEsperEngine(AnomalyPublisher anomalyPublisher,
                                          long freqFlightDelayAnomalyCheckWindow,
                                          int freqFlightDelayAnomalyThresholdCount) {

        this.anomalyPublisher = anomalyPublisher;
        this.freqFlightDelayAnomalyCheckWindow = freqFlightDelayAnomalyCheckWindow;
        this.freqFlightDelayAnomalyThresholdCount = freqFlightDelayAnomalyThresholdCount;
        configure();
    }

    public void configure() {

        configureExpectedEvents();
        configureEsperRules();

    }

    private void configureExpectedEvents() {

        Configuration configuration = new Configuration();
        configuration.addEventType(FLIGHT_DELAY_MESSAGE, FlightDelayMessage.class.getName());

        epServiceProvider = EPServiceProviderManager.getProvider(FLIGHT_DELAY_MESSAGE_ENGINE_URI, configuration);
    }

    private void configureEsperRules() {

        FrequentFlightDelayEsperRule frequentFlightDelayEsperRule = new FrequentFlightDelayEsperRule(epServiceProvider.getEPAdministrator(),
                freqFlightDelayAnomalyCheckWindow,
                freqFlightDelayAnomalyThresholdCount);

        frequentFlightDelayEsperRule.addListener(new FrequentFlightDelayAnomalyListener(anomalyPublisher));
    }

    public void sendEvent(FlightDelayMessage flightDelayMessage) {
        epServiceProvider.getEPRuntime().sendEvent(flightDelayMessage);
    }
}
