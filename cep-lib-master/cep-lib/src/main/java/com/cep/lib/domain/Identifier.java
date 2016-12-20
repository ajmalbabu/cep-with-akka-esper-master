package com.cep.lib.domain;

public class Identifier {
    private String id;


    public Identifier() {
    }


    public Identifier(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "id='" + id + '\'' +
                '}';
    }
}