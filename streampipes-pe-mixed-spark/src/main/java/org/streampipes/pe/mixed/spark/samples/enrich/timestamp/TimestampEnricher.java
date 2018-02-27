package org.streampipes.pe.mixed.spark.samples.enrich.timestamp;

import org.apache.spark.api.java.function.FlatMapFunction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2018-01-22.
 */
public class TimestampEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>>, Serializable {
    private static final long SerialVersionUID = 1L;

    @Override
    public Iterator<Map<String, Object>> call(Map<String, Object> in) throws Exception {
        in.put("appendedTime", System.currentTimeMillis());
        System.out.println(in.toString());

        return Arrays.asList(in).iterator();

    }
}
