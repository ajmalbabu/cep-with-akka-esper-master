package com.cep.lib.service;

import akka.actor.ActorRef;
import akka.cluster.sharding.ShardRegion;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;
import akka.persistence.DeleteMessagesSuccess;
import akka.persistence.UntypedPersistentActor;
import com.cep.lib.domain.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.cep.lib.domain.ResponseMessage.ResponseType.MessageProcessed;

/**
 * A base class actor that makes event sourcing easier. Implements Time-to-live and message expiry out of the box.
 */
public abstract class CepPersistenceActor extends UntypedPersistentActor {

    public static final String ACTOR_TIME_TO_LIVE_SECONDS = "actor.TTL.seconds";
    public static final String ACTOR_TIME_TO_LIVE_MINUTES = "actor.TTL.minutes";
    public static final String ACTOR_STATE_MESSAGE_EXPIRY_TIME_MINUTES = "actor.state.message.expiry.minutes";
    public static final String ACTOR_STATE_MESSAGE_EXPIRY_TIME_SECONDS = "actor.state.message.expiry.seconds";
    public static final String ACTOR_STATE_MESSAGE_EXPIRY_TIME_MILLIS = "actor.state.message.expiry.millis";

    protected DiagnosticLoggingAdapter LOGGER = Logging.getLogger(this);

    protected TransactionId transactionId = TransactionId.instance();

    /**
     * Each time the actor gets created or re-created a new incarnation request with the time element is added into this list.
     */
    private List<IncarnationMessage> incarnationMessages = new ArrayList<>();

    /**
     * Gets called if/when time-to-live is configured & reached.
     */
    private boolean passivateActor;

    /**
     * If message expiry frequency is set, then this listener is used to clean-up the message.
     */
    private MessageExpiryListener messageExpiryListener;

    /**
     * Captures the last received command CEP message.
     */
    protected CepMessage lastCepCommandMessage;


    public CepPersistenceActor() {
        initialize();
    }

    private void initialize() {

        IncarnationMessage incarnationMessage = new IncarnationMessage();

        ScheduleInfo scheduleInfo = new ScheduleInfo(Instant.now(), incarnationMessage, 1, TimeUnit.MILLISECONDS);

        scheduleInfo.schedule(getContext());
    }

    /**
     * Handle message is if it is not already processed. Set and unset LOGGER MDC context.
     *
     * @param msg - Message to be recovered.
     */
    @Override
    public void onReceiveRecover(Object msg) {

        LOGGER.info("ReceiveRecover on actor {} - {} with message: {}", self(), persistenceId(), msg);

        try {

            if (msg instanceof IncarnationMessage) {

                incarnationMessages.add((IncarnationMessage) msg);

            } else if (msg instanceof CepMessage) {

                receiveRecoverCepMessage((CepMessage) msg);

            } else {
                unhandled(msg);
            }

        } finally {
            unsetMdc();
        }

    }

    private void receiveRecoverCepMessage(CepMessage cepMessage) {

        setMdc(cepMessage);

        handleReceiveRecover(cepMessage);
    }

    /**
     * Handle message is if it is not already processed. Set and unset LOGGER MDC context.
     *
     * @param msg - Message to be processed.
     */
    @Override
    public void onReceiveCommand(Object msg) {

        LOGGER.info("ReceiveCommand on actor {} with message: {}", persistenceId(), msg);

        try {

            setMdc(msg);

            if (msg instanceof IncarnationMessage) {

                handleIncarnation((IncarnationMessage) msg);

            } else if (msg instanceof StopMessage) {

                handleActorStop();

            } else if (msg instanceof MessageExpiryRequest) {

                handleMessageExpiry((MessageExpiryRequest) msg);

            } else if (msg instanceof ActorRemovalRequest) {

                handleActorRemovalRequest();

            } else if (msg instanceof CepMessage) {

                handleCepMessage(msg);

            } else if (msg instanceof DeleteMessagesSuccess) {

                handleDeleteMessageSuccess(msg);
            }

        } finally {
            unsetMdc();
        }

    }

    private void handleIncarnation(IncarnationMessage incarnationMessage) {
        store(incarnationMessage, this::scheduleTtlAndExpiry);
    }

    private void scheduleTtlAndExpiry(IncarnationMessage incarnationMessage) {

        incarnationMessages.add(incarnationMessage);
        scheduleTtl();
        scheduleMessageExpiry();

    }

    /**
     * Registers the actor to be automatically garbage collected/removed at a later timeToLive if actor implements
     * TimeToLive interface. If a node failure happens and actor gets started on another node. Then actor removal
     * time gets adjusted by considering the original actor incarnation time.
     */
    private void scheduleTtl() {

        if (TimeToLive.class.isAssignableFrom(getClass())) {

            Time ttl = ((TimeToLive) this).actorTtl();

            ScheduleInfo scheduleInfo = new ScheduleInfo(incarnationMessages.get(0).getCreateTime(),
                    new ActorRemovalRequest(), ttl.time, ttl.timeUnit);

            scheduleInfo.schedule(getContext());
        }
    }

    /**
     * Registers for message expiry on a sliding window basis if actor implements MessageExpiry interface.
     * If a node failure happens and actor gets started on another node. Then message expiry time gets
     * adjusted by considering the original actor incarnation time.
     */
    private void scheduleMessageExpiry() {

        if (MessageExpiry.class.isAssignableFrom(getClass())) {

            MessageExpiry messageExpiry = (MessageExpiry) this;
            messageExpiryListener = messageExpiry.messageExpiryListener();

            MessageExpiryRequest messageExpiryRequest = new MessageExpiryRequest(messageExpiry.expiryTime());

            ScheduleInfo scheduleInfo = new ScheduleInfo(incarnationMessages.get(incarnationMessages.size() - 1).getCreateTime(),
                    messageExpiryRequest, messageExpiry.expiryTime().time, messageExpiry.expiryTime().time,
                    messageExpiry.expiryTime().timeUnit);

            scheduleInfo.schedule(getContext());
        }

    }

    private void handleCepMessage(Object msg) {

        lastCepCommandMessage = (CepMessage) msg;

        if (msg instanceof HeartBeatMessage) {

            LOGGER.info("Received heart beat {}", msg);
            sender().tell(new ResponseMessage(MessageProcessed, null), ActorRef.noSender());

        } else {
            LOGGER.debug("Continue the process {}", lastCepCommandMessage);
            handleReceiveCommand(lastCepCommandMessage);
        }
    }

    private void handleMessageExpiry(MessageExpiryRequest messageExpiryRequest) {

        LOGGER.info("Received message expiry request @ {}. Find highest seq number ", persistenceId());
        long toSequenceNr = messageExpiryListener.expirySequenceNr(messageExpiryRequest.getExpiryTime());

        if (toSequenceNr == Long.MIN_VALUE) {
            LOGGER.info("Do not cleanupState messages @ {}. None found to cleanupState", persistenceId());
        } else {
            LOGGER.info("Expire messages @ {}. for anything <= seq number: {}", persistenceId(), toSequenceNr);
            deleteMessages(toSequenceNr);
        }
    }

    private void handleActorRemovalRequest() {

        LOGGER.info("Received actor clean-up request, remove event store elements and passivate actor {}", persistenceId());

        if (lastSequenceNr() > 0) {
            LOGGER.info("Remove all event sourced message for this actor {}", persistenceId());
            passivateActor = true;
            deleteMessages(lastSequenceNr());
        } else {
            checkAndPassivate(true);
        }
    }

    private void handleDeleteMessageSuccess(Object msg) {

        DeleteMessagesSuccess deleteMessageSuccess = (DeleteMessagesSuccess) msg;
        LOGGER.info("Clean up state @ {}. up to seq number {}", persistenceId(), deleteMessageSuccess.toSequenceNr());
        if (messageExpiryListener != null)
            messageExpiryListener.cleanupState(deleteMessageSuccess.toSequenceNr());

        checkAndPassivate(passivateActor);
    }

    private void checkAndPassivate(boolean passivateActorArg) {

        if (passivateActorArg) {
            LOGGER.info("Send stop message to STOP the actor {}", persistenceId());
            ShardRegion.Passivate passivate = new ShardRegion.Passivate(new StopMessage());
            getContext().parent().tell(passivate, self());
        }
    }

    private void handleActorStop() {
        LOGGER.info("Stop received, stopping actor {}", persistenceId());
        getContext().stop(self());
    }


    /**
     * Persists, updates sequence number of last persisted message and call next function.
     */
    protected <T> void store(CepMessage cepMessage, Consumer<T> nextFunction) {

        persistInternal(cepMessage, nextFunction);
    }

    /**
     * Persists, updates sequence number of last persisted message.
     */
    protected <T> void store(CepMessage cepMessage) {

        persistInternal(cepMessage, CepMessage.noFunction());
    }

    private <T> void persistInternal(CepMessage cepMessage, Consumer<T> nextFunction) {

        persist(cepMessage, msg -> {

            cepMessage.setSequenceNr(lastSequenceNr());

            if (nextFunction != CepMessage.noFunction())
                nextFunction.accept((T) msg);

        });
    }


    protected void acknowledge() {
        sender().tell(new ResponseMessage(MessageProcessed, lastCepCommandMessage), ActorRef.noSender());
    }

    private void setMdc(Object message) {
        if (message instanceof CepMessage) {
            LOGGER.setMDC(((CepMessage) message).getMdc());
            transactionId.setTransactionId(((CepMessage) message).getMdc());
        }
    }

    private void unsetMdc() {
        transactionId.clear();
        LOGGER.clearMDC();
    }

    protected abstract void handleReceiveRecover(CepMessage stateElement);

    protected abstract void handleReceiveCommand(CepMessage cepMessage);


}
