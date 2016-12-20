package com.cep.service.anomaly.esper2;


import com.cep.lib.domain.CepMessage;
import com.cep.lib.domain.Parameters;
import com.cep.lib.service.AnomalyPublisher;
import com.cep.lib.service.CepPersistenceActor;
import com.cep.service.domain.*;
import org.springframework.context.ApplicationContext;

public class FltDelayWithMaintAnomalyDetector extends CepPersistenceActor {

    private static final String ANOMALY_PUBLISHER_BEAN = "anomaly.publisher.bean";

    private AnomalyPublisher anomalyPublisher;
    private FltDelayWithMaintEsperEngine fltDelayWithMaintEsperEngine;

    private FlightDelayWithMaintState state = new FlightDelayWithMaintState();

    public FltDelayWithMaintAnomalyDetector(ApplicationContext applicationContext, Parameters parameters) {

        anomalyPublisher = applicationContext.getBean(parameters.getString(ANOMALY_PUBLISHER_BEAN), AnomalyPublisher.class);
        fltDelayWithMaintEsperEngine = new FltDelayWithMaintEsperEngine(anomalyPublisher);

    }

    @Override
    public String persistenceId() {
        return getSelf().path().parent() + "-" + getSelf().path().name();
    }

    @Override
    public void handleReceiveRecover(CepMessage cepMessage) {

        LOGGER.info("Receive recover {} state: {}", this, state);

        if (cepMessage instanceof FlightDelayMessage) {

            sendFlightDelay((FlightDelayMessage) cepMessage);

        } else if (cepMessage instanceof MaintenanceMessage) {

            sendMaintMessage((MaintenanceMessage) cepMessage);

        } else if (cepMessage instanceof OverrideMessage) {

            sendOverrideMessage((OverrideMessage) cepMessage);
        }
        LOGGER.info("Complete recover of a message. state {}", state);

    }


    @Override
    public void handleReceiveCommand(CepMessage cepMessage) {

        LOGGER.info("Receive command {} state: {}", this, state);

        if (cepMessage instanceof FlightDelayMessage) {

            store(cepMessage, this::sendFlightDelay);

        } else if (cepMessage instanceof MaintenanceMessage) {

            store(cepMessage, this::sendMaintMessage);

        } else if (cepMessage instanceof OverrideMessage) {

            store(cepMessage, this::sendOverrideMessage);
        }

        acknowledge();
    }

    private void sendFlightDelay(FlightDelayMessage flightDelayMessage) {
        state.addFlightDelayMessage(flightDelayMessage);
        fltDelayWithMaintEsperEngine.sendEvent(flightDelayMessage);
    }

    private void sendMaintMessage(MaintenanceMessage maintenanceMessage) {
        state.addMaintenanceMessage(maintenanceMessage);
        fltDelayWithMaintEsperEngine.sendEvent(maintenanceMessage);
    }

    private void sendOverrideMessage(OverrideMessage overrideMessage) {
        state.addOverrideMessage(overrideMessage);
        fltDelayWithMaintEsperEngine.sendEvent(overrideMessage);
    }
}