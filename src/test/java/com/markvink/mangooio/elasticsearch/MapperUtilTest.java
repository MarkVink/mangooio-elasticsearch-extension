package com.markvink.mangooio.elasticsearch;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MapperUtilTest {

    @Test
    public void writeValueAsBytes() throws JsonProcessingException {
        String object = new String("STRING");
        byte[] bytes = MapperUtil.writeValueAsBytes(object);

        assertNotNull(bytes);
    }

    @Test
    public void readValue() throws IOException {
        String object = new String("STRING");
        byte[] bytes = MapperUtil.writeValueAsBytes(object);
        String object2 = (String) MapperUtil.readValue(bytes, String.class);

        assertNotNull(object2, object);
    }
}
