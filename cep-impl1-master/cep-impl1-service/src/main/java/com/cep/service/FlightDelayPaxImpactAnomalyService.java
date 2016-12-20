package com.cep.service;


import akka.actor.ActorRef;
import akka.cluster.sharding.ClusterSharding;
import com.cep.lib.domain.HeartBeatMessage;
import com.cep.lib.service.CepAkkaManager;
import com.cep.service.domain.FlightDelayMessage;
import com.cep.service.domain.FlightEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightDelayPaxImpactAnomalyService {


    @Autowired
    private CepAkkaManager cepAkkaManager;

    public void handleFlightDelay(FlightEvent flightEvent, ActorRef responseActor) {

        ActorRef flightDelayPaxImpactActorShard = flightDelayPaxImpactActorShard();

        FlightDelayMessage flightFrequentDelayDetectionMessage = FlightDelayMessage.createFlightDelayForFrequentDelayDetection(flightEvent);
        flightDelayPaxImpactActorShard.tell(flightFrequentDelayDetectionMessage, responseActor);

    }

    public void ping(FlightEvent flightEvent) {

        ActorRef flightDelayActor = flightDelayPaxImpactActorShard();
        HeartBeatMessage heartBeatMessage = new HeartBeatMessage(flightEvent.flightKey(), flightEvent.flightKey());
        flightDelayActor.tell(heartBeatMessage, ActorRef.noSender());
    }


    private ActorRef flightDelayPaxImpactActorShard() {

        return ClusterSharding.get(cepAkkaManager.getCepActorSystem()).shardRegion("FlightDelayPaxImpactSR");

    }


}
