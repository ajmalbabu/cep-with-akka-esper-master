package com.cep.lib.domain;

import java.io.Serializable;

public class IncarnationMessage extends DefaultCepMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public IncarnationMessage() {
        super(SELF_MESSAGE, SELF_MESSAGE);
    }


    @Override
    public String toString() {
        return "IncarnationMessage{" +
                super.toString() +
                '}';
    }
}
