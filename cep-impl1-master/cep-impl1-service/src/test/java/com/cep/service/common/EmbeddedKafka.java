package com.cep.service.common;


import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.*;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

@Service
public class EmbeddedKafka {

    private EmbeddedZookeeper zkServer;
    private ZkClient zkClient;
    private KafkaServer kafkaServer;

    public String zkConnect(String zkHost) {
        zkServer = new EmbeddedZookeeper();
        return zkHost + ":" + zkServer.port();

    }

    public ZkUtils zookeeper(String zkConnect) {
        zkClient = new ZkClient(zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
        ZkUtils zkUtils = ZkUtils.apply(zkClient, false);
        return zkUtils;
    }


    public KafkaServer kafkaServer(String zkConnect, String kafkaBootstrapServers) throws IOException {

        Properties brokerProps = new Properties();
        brokerProps.setProperty(KafkaConfig.ZkConnectProp(), zkConnect);
        brokerProps.setProperty(KafkaConfig.BrokerIdProp(), "0");
        Path path = Files.createTempDirectory("kafka-" + new Date().getTime() + "-" + new Random().nextInt(99999));
        brokerProps.setProperty(KafkaConfig.LogDirsProp(), path.toAbsolutePath().toString());
        brokerProps.setProperty(KafkaConfig.ListenersProp(), "PLAINTEXT://" + kafkaBootstrapServers);
        KafkaConfig config = new KafkaConfig(brokerProps);
        Time mock = new MockTime();
        kafkaServer = TestUtils.createServer(config, mock);
        return kafkaServer;
    }

    public void createTopic(ZkUtils zkUtils, String topicName) {
        AdminUtils.createTopic(zkUtils, topicName, 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);
    }

    public void shutDown() throws IOException {
        kafkaServer.shutdown();
        zkClient.close();
        zkServer.shutdown();

        FileUtil.removeDirectory(Files.createTempDirectory("kafka-").getParent());

    }


    public <K, V> KafkaProducer<K, V> producer(String kafkaBootstrapServers,
                                               String keySerializer, String valueSerializer) {
        Properties producerProps = new Properties();
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return new KafkaProducer<K, V>(producerProps);
    }

    public <K, V> KafkaConsumer<K, V> consumer(String topic, String kafkaBootstrapServers,
                                               String keyDeserializer, String valueDeserializer) {

        Properties consumerProps = new Properties();
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group0");
        consumerProps.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "consumer0");
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Consumer starts from beginning of topic
        KafkaConsumer<K, V> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }


    static class FileUtil {

        private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

        public static void removeDirectory(Path path) {

            try {
                Files.list(path).filter(p -> p.toString().contains("kafka-")).forEach((p) -> {
                    removeDirectory(p.toFile());
                });
            } catch (Exception e) {
                LOGGER.warn("Error removing directory: {}", e.toString());
            }

        }

        public static void removeDirectory(File dir) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    for (File aFile : files) {
                        removeDirectory(aFile);
                    }
                }
                dir.delete();
            } else {
                dir.delete();
            }
        }
    }

}
