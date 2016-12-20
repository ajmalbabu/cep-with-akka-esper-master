package com.cep.lib.service;

import com.cep.lib.domain.Anomaly;
import com.cep.lib.domain.CepMessage;
import com.cep.lib.domain.Parameters;
import com.cep.lib.domain.ScheduleInfo;
import org.springframework.context.ApplicationContext;

import java.time.Instant;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A sample anomaly detector to test out certain features of cep library. This anomaly detector receives SampleDelayEvent(s)
 * and generate anomaly when two SampleDelayEvent happen within 4 secs. But, if a SampleCancelEvent occurs within 4 secs
 * after a SampleDelayEvent, anomaly gets cancelled. Following scenario can be tested with this implementation.
 * <p>
 * 1. Anomaly is generated correctly?
 * 2. Anomaly gets canceled if CancelEvents is received on time?
 * 3. If anomaly's 4 secs windows gets initiated in one of the nodes in a 3 node cluster and say the minimum
 * cluster size is 2 and if the original node dies the anomaly should gets transfered to other available node
 * in the cluster.
 * 4. Anomaly detector gets cleaned up after time to live.
 */
public class SampleAnomalyDetector extends CepPersistenceActor {

    public static final String ANOMALY_PUBLISHER_BEAN = "anomaly.publisher.bean";
    private static final String ANOMALY_TRIGGER_TIME_SECS = "anomaly.trigger.time.secs";
    private SampleAnomalyState sampleAnomalyState = new SampleAnomalyState();
    private AnomalyPublisher anomalyPublisher;
    private long anomalyTriggerTimeSecs;

    public SampleAnomalyDetector(ApplicationContext applicationContext, Parameters parameters) {
        this.anomalyPublisher = applicationContext.getBean(parameters.getString(ANOMALY_PUBLISHER_BEAN), AnomalyPublisher.class);
        this.anomalyTriggerTimeSecs = parameters.parseLong(ANOMALY_TRIGGER_TIME_SECS);

    }

    @Override
    protected void handleReceiveRecover(CepMessage cepMessage) {
        if (cepMessage instanceof SampleDelayEvent) {

            handleDelay((SampleDelayEvent) cepMessage);

        } else if (cepMessage instanceof SampleCancelEvent) {

            handleCancel((SampleCancelEvent) cepMessage);

        } else if (cepMessage instanceof SampleAnomalyEvent) {

            handleAnomaly(cepMessage);
        }
    }

    @Override
    protected void handleReceiveCommand(CepMessage cepMessage) {

        if (cepMessage instanceof SampleDelayEvent) {

            store(cepMessage, this::handleDelay);

        } else if (cepMessage instanceof SampleCancelEvent) {

            store(cepMessage, this::handleCancel);

        } else if (cepMessage instanceof SampleAnomalyEvent) {

            store(cepMessage, this::handleAnomaly);
        }
        acknowledge();
    }


    private void handleDelay(SampleDelayEvent sampleDelayEvent) {

        sampleAnomalyState.addDelayEvent(sampleDelayEvent);

        LOGGER.info(sampleAnomalyState.toString());

        if (!sampleAnomalyState.isAnomalyCheckScheduled()) {

            SampleAnomalyEvent sampleAnomalyEvent = new SampleAnomalyEvent(sampleDelayEvent.getShardId(), sampleDelayEvent.getEntityId());

            ScheduleInfo scheduleInfo = new ScheduleInfo(Instant.now(), sampleAnomalyEvent, anomalyTriggerTimeSecs, SECONDS);

            scheduleInfo.schedule(getContext());

            sampleAnomalyState.setAnomalyCheckScheduled(true);
        }
    }


    private void handleCancel(SampleCancelEvent sampleCancelEvent) {
        sampleAnomalyState.setSampleCancelEvent(sampleCancelEvent);
    }

    private void handleAnomaly(CepMessage cepMessage) {

        LOGGER.info(sampleAnomalyState.toString());
        if (sampleAnomalyState.hasAnomaly()) {
            Anomaly<String> anomaly = new Anomaly<>("Anomaly detected");
            anomalyPublisher.publish(anomaly);
        }


    }

    @Override
    public String persistenceId() {
        return getSelf().path().parent() + "-" + getSelf().path().name();
    }


}
