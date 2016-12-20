package com.cep.lib.domain;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CepAkkaConfig {

    @Value("${cep.akka.config.file.name:}")
    private String cepAkkaConfigFileName;

    @Value("${cep.akka.actor.system.name:CepActorSystem}")
    private String cepActorSystemName;

    public String getCepAkkaConfigFileName() {
        return cepAkkaConfigFileName;
    }

    public void setCepAkkaConfigFileName(String cepAkkaConfigFileName) {
        this.cepAkkaConfigFileName = cepAkkaConfigFileName;
    }

    public String getCepActorSystemName() {
        return cepActorSystemName;
    }

    public void setCepActorSystemName(String cepActorSystemName) {
        this.cepActorSystemName = cepActorSystemName;
    }
}
