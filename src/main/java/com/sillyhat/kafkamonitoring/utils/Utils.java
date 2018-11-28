package com.sillyhat.kafkamonitoring.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utils {

    private static final ObjectMapper om;

    static {
        JsonFactory jfactory = new JsonFactory();
        jfactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        jfactory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        om = new ObjectMapper(jfactory);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public static String toJson(Object object){
        try{
            return om.writeValueAsString(object);
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
            return "object mapper mapping object ot json failed";
        }
    }

    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-","").toUpperCase();
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd");

    public static String getToday(){
        return sdf.format(new Date());
    }

    public static String formateDate(Date date){
        return sdf.format(date);
    }

}
