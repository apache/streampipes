package org.streampipes.wrapper.spark.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.function.FlatMapFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2018-01-11.
 */
public class JsonToMapFormat implements FlatMapFunction<ConsumerRecord<String, String>, Map<String, Object>> {
    private static final long serialVersionUID = 1L;
    private ObjectMapper mapper;

    public JsonToMapFormat() {
        this.mapper = new ObjectMapper();
    }

    @Override
    public Iterator<Map<String, Object>> call(ConsumerRecord<String, String> s) throws Exception {
        HashMap json = mapper.readValue(s.value(), HashMap.class);

        System.out.println(s.value());

        return Arrays.asList((Map<String, Object>)json).iterator();
    }
}
