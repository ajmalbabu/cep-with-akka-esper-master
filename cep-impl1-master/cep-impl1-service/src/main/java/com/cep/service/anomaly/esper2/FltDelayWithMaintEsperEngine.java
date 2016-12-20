package com.cep.service.anomaly.esper2;

import com.cep.lib.service.AnomalyPublisher;
import com.cep.service.domain.FlightDelayMessage;
import com.cep.service.domain.MaintenanceMessage;
import com.cep.service.domain.OverrideMessage;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;


public class FltDelayWithMaintEsperEngine {

    public static final String FLIGHT_DELAY_MESSAGE = "FlightDelayMessage";
    public static final String MAINTENANCE_EVENT = "MaintenanceMessage";
    public static final String OVERRIDE_EVENT = "OverrideMessage";
    public static final String FLIGHT_DELAY_WITH_MAINX_MESSAGE_ENGINE_URI = "FlightDelayWithMaintEngineUri";

    private AnomalyPublisher anomalyPublisher;
    private EPServiceProvider epServiceProvider;

    public FltDelayWithMaintEsperEngine(AnomalyPublisher anomalyPublisher) {

        this.anomalyPublisher = anomalyPublisher;
        configure();
    }

    public void configure() {

        configureExpectedEvents();
        configureEsperRules();

    }

    private void configureExpectedEvents() {

        Configuration configuration = new Configuration();
        configuration.addEventType(FLIGHT_DELAY_MESSAGE, FlightDelayMessage.class.getName());
        configuration.addEventType(MAINTENANCE_EVENT, MaintenanceMessage.class.getName());
        configuration.addEventType(OVERRIDE_EVENT, OverrideMessage.class.getName());

        epServiceProvider = EPServiceProviderManager.getProvider(FLIGHT_DELAY_WITH_MAINX_MESSAGE_ENGINE_URI, configuration);
    }

    private void configureEsperRules() {

        FltDelayWithMaintEsperRule fltDelayWithMaintEsperRule = new FltDelayWithMaintEsperRule(epServiceProvider.getEPAdministrator());

        fltDelayWithMaintEsperRule.addListener(new FrequentFlightDelayWithMaintListener(anomalyPublisher));
    }

    public void sendEvent(FlightDelayMessage flightDelayMessage) {
        epServiceProvider.getEPRuntime().sendEvent(flightDelayMessage);
    }

    public void sendEvent(MaintenanceMessage maintenanceMessage) {
        epServiceProvider.getEPRuntime().sendEvent(maintenanceMessage);
    }

    public void sendEvent(OverrideMessage overrideMessage) {
        epServiceProvider.getEPRuntime().sendEvent(overrideMessage);
    }

}
