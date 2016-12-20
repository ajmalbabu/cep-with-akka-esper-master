package com.cep.service.messagehandler;


import com.cep.service.dao.FlightDao;
import com.cep.service.dao.PassengerDao;
import com.cep.service.domain.PassengerOnFlightEvent;
import com.cep.service.property.KafkaProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Properties;

@Service
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kafka.passenger.on.flight.event")
public class PassengerOnFlightEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassengerOnFlightEventListener.class);

    @Autowired
    private KafkaProperties kafkaProperties;


    @Autowired
    private PassengerDao passengerDao;

    @Autowired
    private FlightDao flightDao;

    private Properties consumerConfig = new Properties();

    private KafkaConsumer<String, PassengerOnFlightEvent> kafkaConsumer;

    public Properties getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(Properties consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @PostConstruct
    public void postConstruct() throws InterruptedException {

        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getKafkaBootstrapServers());
        kafkaConsumer = new KafkaConsumer<>(consumerConfig);

        // Subscribe to all partition of topic. use 'assign' to subscribe to specific partition.
        kafkaConsumer.subscribe(Arrays.asList(kafkaProperties.getPassengerOnFlightEventTopic()));

        try {
            registerListener(kafkaConsumer);
        } catch (Exception e) {
            LOGGER.error("Error registering: {}", e);
        }

    }

    private void registerListener(final KafkaConsumer<String, PassengerOnFlightEvent> consumer) {

        // TODO Assign a thread-pool for Kafka listeners and use threads from there instead of using random threads.
        new Thread(new Runnable() {
            @Override
            public void run() {
                listen(consumer);
            }
        }).start();

    }

    private void listen(KafkaConsumer<String, PassengerOnFlightEvent> consumer) {

        while (!kafkaProperties.isShutDownHook()) {

            ConsumerRecords<String, PassengerOnFlightEvent> records = consumer.poll(kafkaProperties.getPassengerOnFlightPollIntervalMs());

            for (ConsumerRecord<String, PassengerOnFlightEvent> record : records) {

                LOGGER.info("Received PassengerOnFlightEvent event key: {}, offset: {}, message: {}"
                        , record.key(), record.offset(), record.value());

                passengerDao.save(record.value());

                // TODO - This step can be send to a cluster singleton actor who owns & update a set of flights
                // to avoid race conditions during concurrent passengerOnFlight event updates. So that these listener instances can
                // be scaled up independently.
                PassengerOnFlightEvent passengerOnFlightEvent = record.value();
                flightDao.updateConfirmationNumber(passengerOnFlightEvent.getFlightInfo().flightKey(), passengerOnFlightEvent.getPassenger());
            }

        }
    }
}

