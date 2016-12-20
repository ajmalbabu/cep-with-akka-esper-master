package com.cep.service;


import akka.actor.ActorRef;
import akka.cluster.sharding.ClusterSharding;
import com.cep.lib.service.CepAkkaManager;
import com.cep.service.domain.FlightDelayMessage;
import com.cep.service.domain.FlightEvent;
import com.cep.service.domain.MaintenanceMessage;
import com.cep.service.domain.OverrideMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsperAnomalyService {


    @Autowired
    private CepAkkaManager cepAkkaManager;

    public void handleFlightDelay(FlightEvent flightEvent, ActorRef responseActor) {

        FlightDelayMessage flightFrequentDelayDetectionMessage = FlightDelayMessage.createFlightDelayForFrequentDelayDetection(flightEvent);

        freqFlightDelayActorShard().tell(flightFrequentDelayDetectionMessage, responseActor);

        fltDelayWithMaintImpactShardActor().tell(flightFrequentDelayDetectionMessage, responseActor);
    }

    public void handleMaintenanceMessage(FlightEvent flightEvent, ActorRef responseActor) {

        ActorRef fltDelayWithMaintImpactShardActor = fltDelayWithMaintImpactShardActor();

        fltDelayWithMaintImpactShardActor.tell(new MaintenanceMessage(flightEvent.getFlightInfo().flightKey(), flightEvent.getFlightInfo()), responseActor);
    }

    public void handleOverrideEvent(FlightEvent flightEvent, ActorRef responseActor) {

        ActorRef fltDelayWithMaintImpactShardActor = fltDelayWithMaintImpactShardActor();

        fltDelayWithMaintImpactShardActor.tell(new OverrideMessage(flightEvent.getFlightInfo().flightKey(), flightEvent.getFlightInfo()), responseActor);
    }

    private ActorRef freqFlightDelayActorShard() {

        return ClusterSharding.get(cepAkkaManager.getCepActorSystem()).shardRegion("FreqFlightDelaySR");


    }

    private ActorRef fltDelayWithMaintImpactShardActor() {

        return ClusterSharding.get(cepAkkaManager.getCepActorSystem()).shardRegion("FltDelayWithMaintSR");

    }


}
