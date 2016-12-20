package com.cep.service.anomaly.akka;

import akka.actor.ActorRef;
import akka.routing.FromConfig;
import com.cep.lib.domain.Anomaly;
import com.cep.lib.domain.ResponseMessage;
import com.cep.lib.service.CepAkkaManager;
import com.cep.lib.service.ObservableAnomalyPublisher;
import com.cep.lib.service.SpringExtension;
import com.cep.service.FlightDelayPaxImpactAnomalyService;
import com.cep.service.common.SpringConfig;
import com.cep.service.dataservice.PnrService;
import com.cep.service.domain.AffectedPnr;
import com.cep.service.domain.FlightEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static com.cep.lib.domain.ResponseMessage.ResponseType.MessageProcessed;
import static com.cep.lib.service.ResponseHandlerActor.*;
import static com.cep.service.domain.FlightEvent.createFlightEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Create each anomaly detection in different test class, if this test case is kept in the same test class as the other
 * flight delay test cases (e.g. in FlightDelayGenerateAnomalyTest.java) then there could be issues if all test cases
 * are run concurrently because spring singleton beans. E.g. Issue will be caused by ObservableAnomalyPublisher, this
 * class is purely used to help during testing: basically helps to collects the anomaly published by anomaly detector
 * and this class is a singleton.
 * <p>
 * Because spring beans gets wired only ones and if multiple test cases are fired concurrently within a JVM, then
 * test cases would update the anomaly inside ObservableAnomalyPublisher.java concurrently and it could create race
 * condition during testing.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class FlightDelayCancelAnomalyTest {


    @Autowired
    private PnrService pnrService;

    @Autowired
    private FlightDelayPaxImpactAnomalyService flightDelayPaxImpactAnomalyService;

    @Autowired
    private ObservableAnomalyPublisher observableAnomalyPublisher;

    @Autowired
    private CepAkkaManager cepAkkaManager;

    @Autowired
    private SpringExtension springExtension;

    @Profile("test")
    @Configuration
    public static class FlightDelayTestMocks {

        @Bean
        @Primary
        public PnrService pnrService() {
            return Mockito.mock(PnrService.class);
        }
    }

    @Test
    public void repeatedFlightDelayWithDelayCorrectionShouldCancelAnomaly() throws Exception {

        // Given
        List<String> pnrs = Arrays.asList("ERX5RT", "ZZ11EQ");
        when(pnrService.findAffectedPnrs(any())).thenReturn(new AffectedPnr(pnrs, "WN-NYC-SAN-2016-01-18"));
        FlightEvent flightEvent = createFlightEvent(100, "NYC", "SAN", "2016-01-18", "2016-01-01 08:00:00", "2016-01-01 11:00:00");

        // When
        ActorRef responseActor = springExtension.actorOf(cepAkkaManager.getCepActorSystem(), RESPONSE_HANDLER_ACTOR,
                new FromConfig(), RESPONSE_HANDLER_ACTOR_DISPATCHER);
        flightDelayPaxImpactAnomalyService.handleFlightDelay(flightEvent, responseActor);
        ResponseMessage responseMessage = blockForResponse(responseActor, 250, 6000);
        assertThat(responseMessage.getResponseType()).isEqualTo(MessageProcessed);

        flightEvent = createFlightEvent(100, "NYC", "SAN", "2016-01-18", "2016-01-01 07:00:00", "2016-01-01 10:00:00");
        ActorRef responseActor1 = springExtension.actorOf(cepAkkaManager.getCepActorSystem(), RESPONSE_HANDLER_ACTOR,
                new FromConfig(), RESPONSE_HANDLER_ACTOR_DISPATCHER);
        flightDelayPaxImpactAnomalyService.handleFlightDelay(flightEvent, responseActor1);
        responseMessage = blockForResponse(responseActor1, 250, 6000);
        List<Anomaly> anomalies = observableAnomalyPublisher.blockForAnomalies(250, 3000);

        // Then
        assertThat(responseMessage.getResponseType()).isEqualTo(MessageProcessed);
        assertThat(anomalies.size()).isEqualTo(0);

    }


}
