package com.cep.lib.domain;

import java.io.Serializable;
import java.util.Map;

import static com.cep.lib.domain.ResponseMessage.ResponseType.MessageProcessed;
import static com.cep.lib.domain.ResponseMessage.ResponseType.UnknownMessage;


public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private ResponseType responseType;
    private CepMessage cepMessage;

    public ResponseMessage(ResponseType responseType, CepMessage cepMessage) {
        this.responseType = responseType;
        this.cepMessage = cepMessage;
    }

    public CepMessage getCepMessage() {
        return cepMessage;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public Map<String, Object> getMdc() {
        return cepMessage.getMdc();
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                ", responseType=" + responseType +
                ", cepMessage=" + cepMessage +
                '}';
    }

    public boolean isCompleted() {
        return responseType == MessageProcessed || responseType == UnknownMessage;
    }

    public static enum ResponseType {MessageSubmitted, MessageProcessed, UnknownMessage}
}
