package org.streampipes.pe.sources.samples.biggis;

import org.streampipes.pe.sources.samples.adapter.AdapterSchemaTransformer;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

public class BiggisConcatLineTransformer implements AdapterSchemaTransformer {
    @Override
    public Map<String, Object> transform(Object[] data, boolean withLabel) {
        Map<String, Object> result = new HashedMap();

        if (withLabel) {
            result.put("label", Double.parseDouble((String) data[0]));
        }

        result.put("red0" , Double.parseDouble((String) data[1]));
        result.put("green0" , Double.parseDouble((String) data[2]));
        result.put("blue0" , Double.parseDouble((String) data[3]));
        result.put("nir0" , Double.parseDouble((String) data[4]));

        result.put("blue1" , Double.parseDouble((String) data[5]));
        result.put("green1" , Double.parseDouble((String) data[6]));
        result.put("red1" , Double.parseDouble((String) data[7]));
        result.put("nir1" , Double.parseDouble((String) data[8]));

        result.put("blue2" , Double.parseDouble((String) data[9]));
        result.put("green2" , Double.parseDouble((String) data[10]));
        result.put("red2" , Double.parseDouble((String) data[11]));
        result.put("nir2" , Double.parseDouble((String) data[12]));

        result.put("blue3" , Double.parseDouble((String) data[13]));
        result.put("green3" , Double.parseDouble((String) data[14]));
        result.put("red3" , Double.parseDouble((String) data[15]));
        result.put("nir3" , Double.parseDouble((String) data[16]));

        result.put("blue4" , Double.parseDouble((String) data[17]));
        result.put("green4" , Double.parseDouble((String) data[18]));
        result.put("red4" , Double.parseDouble((String) data[19]));
        result.put("nir4" , Double.parseDouble((String) data[20]));

        result.put("spacial_1" , Double.parseDouble((String) data[21]));
        result.put("spacial_2" , Double.parseDouble((String) data[22]));
        result.put("x_tile" , Double.parseDouble((String) data[23]));
        result.put("y_tile" , Double.parseDouble((String) data[24]));


        return result;
    }
}
