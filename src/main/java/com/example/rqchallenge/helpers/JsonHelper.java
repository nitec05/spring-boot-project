package com.example.rqchallenge.helpers;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T toObject(Map<String, Object> map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

}
