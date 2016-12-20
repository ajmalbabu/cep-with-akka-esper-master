package com.cep.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * To serialize and deserialize the data to and from redis.
 */
@SuppressWarnings("unchecked")
public class CodecUtil<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodecUtil.class);
    private Charset charset = Charset.forName("UTF-8");
    private ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
    private final Class<T> tClass;

    public CodecUtil(Class<T> tClass) {
        this.tClass = tClass;
    }

    public ByteBuffer encodeKey(String s) {
        return charset.encode(s);
    }

    public String decodeKey(ByteBuffer bytes) {
        return charset.decode(bytes).toString();
    }

    public T decodeValue(ByteBuffer bytes) {
        try {
            byte[] byteArray = new byte[bytes.remaining()];
            bytes.get(byteArray);

            return objectMapper.readValue(byteArray, tClass);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public ByteBuffer encodeValue(T object) {
        try {
            return ByteBuffer.wrap(objectMapper.writeValueAsBytes(object));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
