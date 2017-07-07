package de.fzi.cep.sepa.sources.samples.adapter;

import java.util.Map;

public interface AdapterFormatTransformer {

    public byte[] transform(Map<String, Object> data);
}
