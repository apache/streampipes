package de.fzi.cep.sepa.sources.samples.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Map;

public class JsonTransformer  implements AdapterFormatTransformer {

    @Override
    public byte[] transform(Map<String, Object> data) {
        JSONObject json = new JSONObject(data);
        return json.toString().getBytes();
    }
}
