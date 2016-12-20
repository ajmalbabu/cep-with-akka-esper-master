package com.cep.service.anomaly.akka;


import com.cep.lib.domain.*;
import com.cep.lib.service.AnomalyPublisher;
import com.cep.lib.service.CepPersistenceActor;
import com.cep.service.dataservice.PnrService;
import com.cep.service.domain.*;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class FlightDelayPaxImpactAnomalyDetector extends CepPersistenceActor implements TimeToLive, MessageExpiry {

    private static final String ANOMALY_PUBLISHER_BEAN = "anomaly.publisher.bean";
    private static final String ANOMALY_TRIGGER_TIME_SECS = "anomaly.trigger.time.secs";

    private PnrService pnrService;
    private AnomalyPublisher anomalyPublisher;
    private long anomalyTriggerTimeSecs;
    private Time actorTTL;
    private Time messageExpiry;

    private FlightDelayPaxImpactState state = new FlightDelayPaxImpactState();


    public FlightDelayPaxImpactAnomalyDetector(ApplicationContext applicationContext, Parameters parameters) {

        this.pnrService = applicationContext.getBean(PnrService.class);
        this.anomalyPublisher = applicationContext.getBean(parameters.getString(ANOMALY_PUBLISHER_BEAN), AnomalyPublisher.class);
        this.anomalyTriggerTimeSecs = parameters.parseLong(ANOMALY_TRIGGER_TIME_SECS);

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

        if (cepMessage instanceof FlightDelayInducedMessage) {

            handleFlightDelayInducedMessage((FlightDelayInducedMessage) cepMessage);

        } else if (cepMessage instanceof FlightDelayPaxImpactAnomalyTrigger) {

            state.setFlightDelayPaxImpactAnomalyTrigger((FlightDelayPaxImpactAnomalyTrigger) cepMessage);
        }
    }

    @Override
    public void handleReceiveCommand(CepMessage cepMessage) {

        LOGGER.info("Receive command {} state: {}", this, state.toString());
        if (cepMessage instanceof FlightDelayMessage) {

            handleFlightDelay((FlightDelayMessage) cepMessage);

        } else if (cepMessage instanceof FlightDelayPaxImpactAnomalyTrigger) {

            store(cepMessage, this::checkToGenerateAnomaly);

        }
    }


    private void handleFlightDelay(FlightDelayMessage flightDelayMessage) {

        EventType flightDelayEventType = state.findFlightDelayEventType(flightDelayMessage);

        LOGGER.info("Received a {} with {}", flightDelayMessage, flightDelayEventType);

        AffectedPnr affectedPnr = null;
        if (flightDelayEventType.isFirstTimeEvent() || flightDelayEventType.isRepeatedEventTypeWithIncreaseDirection()) {
            // For Repeated Event Type With Increase delay high priority anomaly could be generated.
            affectedPnr = pnrService.findAffectedPnrs(flightDelayMessage);
        }

        FlightDelayInducedMessage flightDelayInducedMessage = new FlightDelayInducedMessage(flightDelayMessage, flightDelayEventType, affectedPnr);
        store(flightDelayInducedMessage, this::handleFlightDelayInducedMessage);
    }

    private void handleFlightDelayInducedMessage(FlightDelayInducedMessage flightDelayInducedMessage) {

        state.addFlightDelayInducedEvent(flightDelayInducedMessage);

        if (flightDelayInducedMessage.hasAffectedPnrs()) {

            LOGGER.info("Scheduling anomaly trigger for {}", flightDelayInducedMessage);

            scheduleAnomalyTriggerEvent(flightDelayInducedMessage);
        } else {
            LOGGER.info("No affected PNRs found, for {}", flightDelayInducedMessage);
        }

        acknowledge();
    }

    private void scheduleAnomalyTriggerEvent(FlightDelayInducedMessage flightDelayInducedMessage) {

        FlightDelayPaxImpactAnomalyTrigger flightDelayPaxImpactAnomalyTrigger = new FlightDelayPaxImpactAnomalyTrigger(
                flightDelayInducedMessage.getShardId(), flightDelayInducedMessage.getEntityId(),
                flightDelayInducedMessage.getResponsibleFlightDelayMessage().getFlightInfo().flightKey());

        ScheduleInfo scheduleInfo = new ScheduleInfo(flightDelayInducedMessage.getCreateTime(),
                flightDelayPaxImpactAnomalyTrigger, anomalyTriggerTimeSecs, SECONDS);

        scheduleInfo.schedule(getContext());

    }


    private void checkToGenerateAnomaly(FlightDelayPaxImpactAnomalyTrigger flightDelayPaxImpactAnomalyTrigger) {

        LOGGER.info("Received {}", flightDelayPaxImpactAnomalyTrigger);

        boolean hasReceivedNewerFlightDelayMessage = state.hasReceivedNewerFlightDelayMessageBeyond(
                flightDelayPaxImpactAnomalyTrigger.getResponsibleFlightDelayInducedFlightKey());

        if (hasReceivedNewerFlightDelayMessage) {
            LOGGER.info("\n **************\nCancel current anomaly generation, There are newer flight delay messages, " +
                    "those either corrected anomaly or added higher priority anomaly. \n ************** ");
        } else {
            generateAnomaly(flightDelayPaxImpactAnomalyTrigger);
        }
    }


    private void generateAnomaly(FlightDelayPaxImpactAnomalyTrigger flightDelayPaxImpactAnomalyTrigger) {

        Optional<FlightDelayInducedMessage> responsibleFlightInducedCepMessage = state.findFlightDelayInducedMessageAt(
                flightDelayPaxImpactAnomalyTrigger.getResponsibleFlightDelayInducedFlightKey());

        Anomaly<AffectedPnr> anomaly = new Anomaly<>(responsibleFlightInducedCepMessage.get().getAffectedPnr());
        anomalyPublisher.publish(anomaly);
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