package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public final class JsonUtil {
    private static ObjectMapper jsonToObjMapper = new ObjectMapper();
    private static ObjectMapper mapper = new ObjectMapper();

    static{
        jsonToObjMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        jsonToObjMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jsonToObjMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigDecimal.class, new BigDecimalSerialize());

        jsonToObjMapper.registerModule(simpleModule);
        registerDateFormat(jsonToObjMapper);
        registerDateFormat(mapper);
    }


    private JsonUtil() {
        //do nothing
    }

    public static ObjectMapper getJsonToObjMapper() {
        return jsonToObjMapper;
    }

    public static <T> T jsonToObject(String jsonInString, Class<T> clz) {
        try {
            return jsonToObjMapper.readValue(jsonInString, clz);
        } catch (Exception e) {
            log.error("{} {} error", jsonInString, clz.getName(), e);
        }
        return null;
    }

    public static String objectToJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("[json] to json error ", e);
            return null;
        }
    }


    public static void registerDateFormat(ObjectMapper objectMapper){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateSerializer localDateSerializer = new LocalDateSerializer(dateFormatter);
        objectMapper.registerModule(new JavaTimeModule()
                .addSerializer(LocalDate.class, localDateSerializer));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(dateTimeFormatter);
        objectMapper.registerModule(new JavaTimeModule()
                .addSerializer(LocalDateTime.class, localDateTimeSerializer));
    }
}
