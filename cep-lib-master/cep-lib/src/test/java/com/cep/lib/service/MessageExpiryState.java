package com.cep.lib.service;


import com.cep.lib.domain.MessageExpiryListener;
import com.cep.lib.domain.Time;

import java.util.ArrayList;
import java.util.List;

public class MessageExpiryState implements MessageExpiryListener {

    private List<MessageExpiryEvent> messageExpiryEvents = new ArrayList<>();

    public void addMessageExpiryEvent(MessageExpiryEvent messageExpiryEvent) {
        messageExpiryEvents.add(messageExpiryEvent);
    }

    public List<MessageExpiryEvent> getMessageExpiryEvents() {
        return messageExpiryEvents;
    }

    @Override
    public long expirySequenceNr(Time windowLength) {
        return highestSequenceNr(messageExpiryEvents, windowLength);
    }

    @Override
    public void cleanupState(long toSequenceNr) {
        messageExpiryEvents = cleanupList(messageExpiryEvents, toSequenceNr);
    }

    public int eventCount() {
        return messageExpiryEvents.size();
    }

    @Override
    public String toString() {
        return "MessageExpiryState{" +
                "messageExpiryEvents=" + messageExpiryEvents +
                '}';
    }
}
