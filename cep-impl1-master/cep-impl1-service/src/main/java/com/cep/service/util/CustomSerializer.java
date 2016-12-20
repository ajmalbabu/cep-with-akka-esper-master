package com.cep.service.util;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * Evaluate AVRO serializer or Kyro @ http://niels.nu/blog/2016/kafka-custom-serializers.html
 */
public class CustomSerializer implements Deserializer<Object>, Serializer<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSerializer.class);

    public CustomSerializer() {

    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Object data) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error serializing {} {}", data, e);
            return new byte[0];
        }
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
        } catch (Exception e) {
            LOGGER.error("Error de-serializing {} {}", data, e);

            return null;
        }
    }

    @Override
    public void close() {

    }
}