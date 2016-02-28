package com.markvink.mangooio.elasticsearch;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;

@Singleton
public final class MapperUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static byte[] writeValueAsBytes(Object object) throws JsonProcessingException {
        return mapper.writeValueAsBytes(object);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object readValue(byte[] bytes, Class objectClass) throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(bytes, objectClass);
    }
}
