package com.cep.lib.domain;


public interface MessageExpiry {

    MessageExpiryListener messageExpiryListener();

    Time expiryTime();

}
