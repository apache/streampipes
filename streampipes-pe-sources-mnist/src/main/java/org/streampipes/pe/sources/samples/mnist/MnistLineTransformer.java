package org.streampipes.pe.sources.samples.mnist;

import org.streampipes.pe.sources.samples.adapter.AdapterSchemaTransformer;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class MnistLineTransformer implements AdapterSchemaTransformer {
    @Override
    public Map<String, Object> transform(Object[] data, boolean withLabel) {
        Map<String, Object> result = new HashedMap();

        if (withLabel) {
            result.put("label" , Double.parseDouble((String) data[0]));
        }

        Double[] image = new Double[data.length - 1];

        for (int i = 0; i < image.length; i++) {
            image[i] = Double.parseDouble((String) data[i + 1]);
        }
        result.put("image", image);

        return result;
    }

}
