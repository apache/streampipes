package de.fzi.cep.sepa.sources.samples.mnist;

import de.fzi.cep.sepa.sources.samples.adapter.AdapterSchemaTransformer;
import org.apache.commons.collections.map.HashedMap;

import java.util.Arrays;
import java.util.Map;

public class MnistLineTransformer implements AdapterSchemaTransformer {
    @Override
    public Map<String, Object> transform(Object[] data) {
        Map<String, Object> result = new HashedMap();

        result.put("label" , Double.parseDouble((String) data[0]));

        Double[] image = new Double[data.length - 1];

        for (int i = 0; i < image.length; i++) {
            image[i] = Double.parseDouble((String) data[i + 1]);
        }
        result.put("image", image);

        return result;
    }
}
