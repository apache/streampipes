package org.streampipes.dataformat;

import org.streampipes.commons.exceptions.SpRuntimeException;

import java.util.Map;

public interface SpDataFormatDefinition {

  String getTransportFormatRdfUri();

  Map<String, Object> toMap(byte[] event) throws SpRuntimeException;

  byte[] fromMap(Map<String, Object> event) throws SpRuntimeException;
}
