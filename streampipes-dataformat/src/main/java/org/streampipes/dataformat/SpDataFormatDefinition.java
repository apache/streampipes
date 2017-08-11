package org.streampipes.dataformat;

import org.streampipes.commons.exceptions.StreamPipesRuntimeException;

import java.util.Map;

public interface SpDataFormatDefinition<IN> {

  String getTransportFormatRdfUri();

  IN fromBytesArray(byte[] event);

  Map<String, Object> toMap(IN event) throws StreamPipesRuntimeException;

  IN fromMap(Map<String, Object> event) throws StreamPipesRuntimeException;
}
