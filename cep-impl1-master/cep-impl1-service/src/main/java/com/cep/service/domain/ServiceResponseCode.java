package com.cep.service.domain;

/**
 * Created by tca049 on 5/3/16.
 * <p>
 * This enum contains the possible info values for the API responses.
 * </p>
 */
public enum ServiceResponseCode {
    OK("OK"),
    RESULT_COLLECTION_INCOMPLETE("RESULT_COLLECTION_INCOMPLETE");

    private String alias;

    ServiceResponseCode(String alias) {
        this.alias = alias;
    }

    public String alias() {
        return this.alias;
    }

    public static ServiceResponseCode fromValue(String v) {
        for (ServiceResponseCode c : ServiceResponseCode.values()) {
            if (c.alias.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
