package com.cep.lib.domain;

import java.util.*;

/**
 * Implement this interface to clean up data in the actor state. This method gets called on
 * the implementor (state object) at every message expiry window length.
 */
public interface MessageExpiryListener {

    /**
     * Return back the sequence number (up to and including) of the messages that should be expired.
     * Refer to test package MessageExpiryDetector.java & MessageExpiryState.java that implements this
     * listener for an example.
     */
    long expirySequenceNr(Time timeWindow);

    /**
     * Clean up the state for all data in the state up-to the toSequenceNr. Refer to test package
     * MessageExpiryDetector.java & MessageExpiryState.java that implements this listener for an example.
     */
    void cleanupState(long toSequenceNr);

    /**
     * Returns the highest sequence number of the provided CepMessage(s). The messages are first filtered out with
     * the following logic. message's create-time < (now - timeWindow) and then from that list, returns the one
     * with highest sequence number.
     */
    default long highestSequenceNr(List<? extends CepMessage> cepMessages, Time timeWindow) {

        List<PersistenceIdentifier> ids = new ArrayList<>();
        for (CepMessage cepMessage : cepMessages) {
            if (cepMessage.isBefore(timeWindow)) {
                ids.add(new PersistenceIdentifier(cepMessage.getShardId(), cepMessage.getEntityId(), cepMessage.getSequenceNr()));
            }
        }
        return PersistenceIdentifier.highestSeqNr(ids);
    }


    /**
     * Cleans up the provided cepMessage(s) list by removing all the messages that is <= provided toSequenceNumber.
     * The provided List is not altered. The returned list is an ArrayList.
     */
    default <T1> List<T1> cleanupList(List<? extends CepMessage> cepMessages, long toSequenceNumber) {

        List<CepMessage> unexpiredMessages = new ArrayList<>();
        for (CepMessage cepMessage : cepMessages) {
            if (cepMessage.getSequenceNr() > toSequenceNumber) {
                unexpiredMessages.add(cepMessage);
            }
        }
        return (List<T1>) unexpiredMessages;
    }

    /**
     * Cleans up the provided cepMessage(s) Set by removing all the messages that is <= provided toSequenceNumber.
     * The provided Set is not altered.  The returned set is a HashSet.
     */
    default <T1> Set<T1> cleanupSet(Set<? extends CepMessage> cepMessages, long toSequenceNumber) {

        Set<CepMessage> unexpiredMessages = new HashSet<>();
        for (CepMessage cepMessage : cepMessages) {
            if (cepMessage.getSequenceNr() > toSequenceNumber) {
                unexpiredMessages.add(cepMessage);
            }
        }
        return (Set<T1>) unexpiredMessages;
    }

    /**
     * Cleans up the provided cepMessage(s) Map by removing all the messages that is <= provided toSequenceNumber.
     * The provided Map is not altered.  The returned set is a HashMap.
     */
    default <T, T1> Map<T, T1> cleanupMap(Map<T, ? extends CepMessage> cepMessages, long toSequenceNumber) {

        Map<T, T1> unexpiredMessages = new HashMap<>();
        for (T key : cepMessages.keySet()) {
            CepMessage cepMessage = cepMessages.get(key);
            if (cepMessage.getSequenceNr() > toSequenceNumber) {
                unexpiredMessages.put(key, (T1) cepMessage);
            }
        }
        return unexpiredMessages;
    }

}
