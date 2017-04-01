package de.fzi.cep.sepa.sources.samples.biggis;

import de.fzi.cep.sepa.sources.samples.adapter.AdapterSchemaTransformer;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class BiggisLineTransformer implements AdapterSchemaTransformer {
    @Override
    public Map<String, Object> transform(Object[] data) {
        Map<String, Object> result = new HashedMap();

        result.put("label" , Double.parseDouble((String) data[0]));
        result.put("blue" , Double.parseDouble((String) data[1]));
        result.put("green" , Double.parseDouble((String) data[2]));
        result.put("red" , Double.parseDouble((String) data[3]));
        result.put("nir" , Double.parseDouble((String) data[4]));
        result.put("spacial_1" , Double.parseDouble((String) data[5]));
        result.put("spacial_2" , Double.parseDouble((String) data[6]));
        result.put("x_tile" , Double.parseDouble((String) data[7]));
        result.put("y_tile" , Double.parseDouble((String) data[8]));


        return result;
    }
}
