package de.fzi.cep.sepa.sources.samples.adapter;

import java.util.Map;

public interface AdapterSchemaTransformer {

    public Map<String, Object> transform(Object[] data);

}
