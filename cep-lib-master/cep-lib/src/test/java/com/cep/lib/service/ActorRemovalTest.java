package com.cep.lib.service;

import akka.actor.ActorRef;
import akka.cluster.sharding.ClusterSharding;
import akka.routing.FromConfig;
import com.cep.lib.domain.ResponseMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.cep.lib.domain.ResponseMessage.ResponseType.MessageProcessed;
import static com.cep.lib.service.ResponseHandlerActor.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * These test cases are time sensitive and may fail if run with test suite because of GC pause etc. Run them as
 * part of separate suite or increase the expiry delay to higher number and adjust test cases delays accordingly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class ActorRemovalTest {

    @Autowired
    private CepAkkaManager cepAkkaManager;

    @Autowired
    private SpringExtension springExtension;

    @Test
    public void actorShouldGetRemovedAndStopped() throws Exception {

        // Given - Setup the actor and send a message to the actor.
        ActorRef actorRemovalDetector = ClusterSharding.get(cepAkkaManager.getCepActorSystem()).shardRegion("ActorRemovalShardRegion");

        ActorRef responseActor1 = springExtension.actorOf(cepAkkaManager.getCepActorSystem(), RESPONSE_HANDLER_ACTOR,
                new FromConfig(), RESPONSE_HANDLER_ACTOR_DISPATCHER);

        MessageExpiryEvent messageExpiryEvent = new MessageExpiryEvent(1, "3", 101);
        actorRemovalDetector.tell(messageExpiryEvent, responseActor1);
        ResponseMessage responseMessage1 = blockForResponse(responseActor1, 10, 5000);

        assertThat(responseMessage1.getResponseType()).isEqualTo(MessageProcessed);

        ActorRef responseActor2 = springExtension.actorOf(cepAkkaManager.getCepActorSystem(), RESPONSE_HANDLER_ACTOR,
                new FromConfig(), RESPONSE_HANDLER_ACTOR_DISPATCHER);
        actorRemovalDetector.tell(new MessageExpiryDetector.StateEventCountReq(1, "3"), responseActor2);
        ResponseMessage responseMessage2 = blockForResponse(responseActor2, 10, 5000);
        assertThat(((MessageExpiryDetector.StateEventCountResp) responseMessage2.getCepMessage()).messageExpiryEvents.size()).isEqualTo(1);
        assertThat(((MessageExpiryDetector.StateEventCountResp) responseMessage2.getCepMessage()).messageExpiryEvents.get(0).getEventId()).isEqualTo(101);

        // When
        // Give time so that actor gets Cleaned-up and Stopped. actor.TTL.seconds = 2 in application.yaml
        Thread.sleep(2100);

        // Then
        // Query the actor again, now it gets restarted, but state will be empty.
        ActorRef responseActor3 = springExtension.actorOf(cepAkkaManager.getCepActorSystem(), RESPONSE_HANDLER_ACTOR,
                new FromConfig(), RESPONSE_HANDLER_ACTOR_DISPATCHER);
        actorRemovalDetector.tell(new MessageExpiryDetector.StateEventCountReq(1, "3"), responseActor3);
        ResponseMessage responseMessage3 = blockForResponse(responseActor3, 10, 5000);
        assertThat(((MessageExpiryDetector.StateEventCountResp) responseMessage3.getCepMessage()).messageExpiryEvents.size()).isEqualTo(0);

    }

}
