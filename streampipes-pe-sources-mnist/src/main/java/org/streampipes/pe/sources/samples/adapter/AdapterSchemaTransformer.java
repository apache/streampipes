package org.streampipes.pe.sources.samples.adapter;

import java.util.Map;

public interface AdapterSchemaTransformer {

    public Map<String, Object> transform(Object[] data, boolean withLabel);

}
