package com.cep.lib.domain;

/**
 * Represent an anomaly message.
 */
public class Anomaly<T> {

    private T message;

    public Anomaly(T message) {
        this.message = message;
    }

    public T getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Anomaly{" +
                "message=" + message +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Anomaly)) return false;

        Anomaly<?> anomaly = (Anomaly<?>) o;

        return message != null ? message.equals(anomaly.message) : anomaly.message == null;

    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }
}
