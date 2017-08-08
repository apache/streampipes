package org.streampipes.pe.mixed.flink.samples.batchstream;

import org.apache.flink.api.common.functions.MapFunction;

import java.util.Map;

/**
 * Created by philippzehnder on 20.02.17.
 */
public class AddUnionKeyMap implements MapFunction<Map<String, Object>, Map<String, Object>> {
    private int key;

    public AddUnionKeyMap(int key) {
        this.key = key;
    }

    @Override
    public Map<String, Object> map(Map<String, Object> value) throws Exception {
        value.put("fbtskey", key);
        return value;
    }
}
