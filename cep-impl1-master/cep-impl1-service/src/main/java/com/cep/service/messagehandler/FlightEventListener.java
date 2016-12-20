package com.cep.service.messagehandler;


import com.cep.service.dao.FlightDao;
import com.cep.service.domain.FlightEvent;
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
@ConfigurationProperties(prefix = "kafka.flight.event")
public class FlightEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightEventListener.class);

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private FlightDao flightDao;

    private Properties consumerConfig = new Properties();

    private KafkaConsumer<String, FlightEvent> kafkaConsumer;

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
        kafkaConsumer.subscribe(Arrays.asList(kafkaProperties.getFlightEventTopic()));

        try {
            registerListener(kafkaConsumer);
        } catch (Exception e) {
            LOGGER.error("Error registering: {}", e);
        }
    }

    private void registerListener(final KafkaConsumer<String, FlightEvent> consumer) {

        // TODO Assign a thread-pool for Kafka listeners and use threads from there instead of using random threads.
        new Thread(new Runnable() {
            @Override
            public void run() {
                listen(consumer);
            }
        }).start();

    }

    private void listen(KafkaConsumer<String, FlightEvent> consumer) {

        while (!kafkaProperties.isShutDownHook()) {

            ConsumerRecords<String, FlightEvent> records = null;
            records = consumer.poll(kafkaProperties.getFlightPollIntervalMs());

            for (ConsumerRecord<String, FlightEvent> record : records) {

                LOGGER.info("Received flight event key: {}, offset: {}, message: {}"
                        , record.key(), record.offset(), record.value());

                flightDao.save(record.value());
            }

        }
    }
}

