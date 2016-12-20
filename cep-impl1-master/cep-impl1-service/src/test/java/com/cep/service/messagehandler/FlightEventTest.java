package com.cep.service.messagehandler;


import com.cep.service.common.EmbeddedKafka;
import com.cep.service.common.IntegrationTest;
import com.cep.service.common.SpringConfig;
import com.cep.service.dao.FlightDao;
import com.cep.service.domain.FlightEvent;
import com.cep.service.domain.FlightInfo;
import kafka.server.KafkaServer;
import kafka.utils.ZkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This test shows how to test the message flow using Kafka topic, both publishing
 * and subscribing.
 * 1. Test case uses kafka configurations from application-service-test.yaml file.
 * 2. The production code would create Publisher (FlightEventPublisher.java) and
 * Consumer(FlightEventListener.java) using this above yaml file.
 * 3. This test case setups a embedded Zookeeper and Kafka server & Topic during
 * the setup.
 * 4. Injects a FlightDao mock class into the FlightEventListener.java class.
 * 5. Publishes a message to the Topic.
 * 6. Which in turn triggers the FlightEventListener to listen for the message
 * (Give a delay of 2 Seconds for it to read the message).
 * 7. Listener interacts with the above FlightDao mock with the message and can be
 * asserted in unit test.
 * 8. Inspiration from https://github.com/asmaier/mini-kafka
 * <p>
 * Note - Some times this test case fails and re-run succeeds, something to do with
 * clean-up process
 */

@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class FlightEventTest {


    @Autowired
    private EmbeddedKafka embeddedKafka;

    @Value("${kafka.bootstrap.servers:localhost:9093}")
    private String kafkaBootstrapServers;

    @Value("${zkHost:127.0.0.1}")
    private String zkHost;

    @Mock
    private FlightDao flightDao;

    @Autowired
    private FlightEventPublisher flightEventPublisher;

    @InjectMocks
    @Autowired
    private FlightEventListener flightEventListener;


    @Before
    public void setup() throws IOException {

        String zkConnect = embeddedKafka.zkConnect(zkHost);
        ZkUtils zkUtils = embeddedKafka.zookeeper(zkConnect);
        KafkaServer kafkaServer = embeddedKafka.kafkaServer(zkConnect, kafkaBootstrapServers);
        embeddedKafka.createTopic(zkUtils, "flightEventTopic");

        // MockitoAnnotations.initMocks(this);
    }

    @After
    public void after() throws IOException {
        embeddedKafka.shutDown();
    }

    @Test
    public void publishAndSubscribeFlightEvent() throws Exception {

        // Given, When
        FlightEvent flightEvent = new FlightEvent();

        FlightInfo flightInfo = new FlightInfo();
        flightInfo.setFlightNumber(100);
        flightInfo.setFlightDate(new SimpleDateFormat("yyyy-mm-dd").parse("2011-01-18"));
        flightInfo.setCarrierCode("WN");
        flightInfo.setDepartureAirport("MSP");
        flightInfo.setArrivalAirport("DAL");

        flightEvent.setFlightInfo(flightInfo);

        flightEventPublisher.publish(flightEvent);

        // Allow enough time for the consumer to listen for the message.
        Thread.sleep(3000);

        // Then
        verify(flightDao, times(1)).save(flightEvent);
    }


}
