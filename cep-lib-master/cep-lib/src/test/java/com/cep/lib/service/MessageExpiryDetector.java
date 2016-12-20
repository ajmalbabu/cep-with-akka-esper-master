package com.cep.lib.service;

import akka.actor.ActorRef;
import com.cep.lib.domain.*;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cep.lib.domain.ResponseMessage.ResponseType.MessageProcessed;

/**
 * Helps to test message expiry implementation.
 */
public class MessageExpiryDetector extends CepPersistenceActor implements MessageExpiry {

    private MessageExpiryState messageExpiryState = new MessageExpiryState();
    private Time messageExpiryTime;

    public MessageExpiryDetector(ApplicationContext applicationContext, Parameters parameters) {
        messageExpiryTime = new Time(parameters.parseLong(ACTOR_STATE_MESSAGE_EXPIRY_TIME_MILLIS), TimeUnit.MILLISECONDS);
    }

    @Override
    protected void handleReceiveRecover(CepMessage cepMessage) {

        if (cepMessage instanceof MessageExpiryEvent) {

            handleMessageExpiryEvent((MessageExpiryEvent) cepMessage);

        }
    }

    @Override
    protected void handleReceiveCommand(CepMessage cepMessage) {

        if (cepMessage instanceof MessageExpiryEvent) {

            store(cepMessage, this::handleMessageExpiryEvent);

            acknowledge();

        } else if (cepMessage instanceof StateEventCountReq) {

            StateEventCountResp stateEventCountResp = new StateEventCountResp(cepMessage.getShardId(), cepMessage.getEntityId(), new ArrayList<>(messageExpiryState.getMessageExpiryEvents()));

            sender().tell(new ResponseMessage(MessageProcessed, stateEventCountResp), ActorRef.noSender());
        }
    }


    private void handleMessageExpiryEvent(MessageExpiryEvent messageExpiryEvent) {

        messageExpiryState.addMessageExpiryEvent(messageExpiryEvent);

    }


    @Override
    public String persistenceId() {
        return getSelf().path().parent() + "-" + getSelf().path().name();
    }

    @Override
    public MessageExpiryListener messageExpiryListener() {
        return messageExpiryState;
    }

    @Override
    public Time expiryTime() {
        return messageExpiryTime;
    }

    static public class StateEventCountReq extends DefaultCepMessage {

        public StateEventCountReq(Object shardId, String entityId) {
            super(shardId, entityId);
        }

        @Override
        public String toString() {
            return "StateEventCountReq{}" +
                    super.toString() +
                    "";
        }
    }

    static public class StateEventCountResp extends DefaultCepMessage {

        public final List<MessageExpiryEvent> messageExpiryEvents;


        public StateEventCountResp(Object shardId, String entityId, List<MessageExpiryEvent> messageExpiryEvents) {
            super(shardId, entityId);
            this.messageExpiryEvents = new ArrayList<>(messageExpiryEvents);
        }

        @Override
        public String toString() {
            return "StateEventCountResp{" +
                    super.toString() +
                    ", messageExpiryEvents=" + messageExpiryEvents +
                    '}';
        }
    }

}
