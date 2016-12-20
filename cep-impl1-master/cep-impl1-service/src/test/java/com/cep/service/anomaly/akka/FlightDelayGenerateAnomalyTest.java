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


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class FlightDelayGenerateAnomalyTest {


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
    public void shouldGenerateFlightDelayAnomalyAfterSomePause() throws Exception {

        // Given
        List<String> pnrs = Arrays.asList("ERX5RT", "ZZ11EE");
        when(pnrService.findAffectedPnrs(any())).thenReturn(new AffectedPnr(pnrs, "WN-NYC-DAL-2016-01-18"));
        FlightEvent flightEvent = createFlightEvent(100, "NYC", "DAL", "2016-01-18", "2016-01-01 08:00:00", "2016-01-01 11:00:00");

        // When
        ActorRef responseActor = springExtension.actorOf(cepAkkaManager.getCepActorSystem(), RESPONSE_HANDLER_ACTOR,
                new FromConfig(), RESPONSE_HANDLER_ACTOR_DISPATCHER);
        flightDelayPaxImpactAnomalyService.handleFlightDelay(flightEvent, responseActor);
        ResponseMessage responseMessage = blockForResponse(responseActor, 250, 5000);
        List<Anomaly> anomalies = observableAnomalyPublisher.blockForAnomalies(250, 5000);

        // Then
        assertThat(responseMessage.getResponseType()).isEqualTo(MessageProcessed);
        assertThat(anomalies.size()).isEqualTo(1);

    }


}
