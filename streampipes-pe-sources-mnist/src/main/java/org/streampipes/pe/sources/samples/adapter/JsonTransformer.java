package org.streampipes.pe.sources.samples.adapter;

import org.json.JSONObject;

import java.util.Map;

public class JsonTransformer  implements AdapterFormatTransformer {

    @Override
    public byte[] transform(Map<String, Object> data) {
        JSONObject json = new JSONObject(data);
        return json.toString().getBytes();
    }
}
