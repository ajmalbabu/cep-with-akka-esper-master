package com.cep.service.domain;


import java.io.Serializable;

public class EventType implements Serializable {
    private static final long serialVersionUID = 1L;

    private Type type = Type.UNSET;
    private Direction direction = Direction.DEFAULT;

    public EventType(Type type) {
        this.type = type;
    }

    public EventType(Type type, Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    public boolean isFirstTimeEvent() {
        return type == Type.FIRST_TIME;
    }

    public boolean isRepeatedEvent() {
        return type == Type.REPEATED;
    }

    public boolean isUnsetEvent() {
        return type == Type.UNSET;
    }

    public boolean isDecreaseDirection() {
        return direction == Direction.DECREASE;
    }

    public boolean isIncreaseDirection() {
        return direction == Direction.INCREASE;
    }

    public boolean isDefaultDirection() {
        return direction == Direction.DEFAULT;
    }


    public boolean isRepeatedEventTypeWithIncreaseDirection() {
        return type == Type.REPEATED && direction == Direction.INCREASE;
    }

    public enum Type {
        UNSET, FIRST_TIME, REPEATED
    }

    public enum Direction {
        DEFAULT, INCREASE, DECREASE
    }

    @Override
    public String toString() {
        return "EventType{" +
                "type=" + type +
                ", direction=" + direction +
                '}';
    }
}
