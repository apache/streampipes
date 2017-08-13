package org.streampipes.dataformat.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.model.vocabulary.MessageFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonDataFormatDefinition implements SpDataFormatDefinition {

  private ObjectMapper objectMapper;

  public JsonDataFormatDefinition() {
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public String getTransportFormatRdfUri() {
    return MessageFormat.Json;
  }


  @Override
  public Map<String, Object> toMap(byte[] event) throws SpRuntimeException {
    try {
      return objectMapper.readValue(new String(event), HashMap.class);
    } catch (IOException e) {
      throw new SpRuntimeException("Could not convert event to map data structure");
    }
  }

  @Override
  public byte[] fromMap(Map<String, Object> event) throws SpRuntimeException {
    try {
      return objectMapper.writeValueAsBytes(event);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException("Could not convert map data structure to JSON string");
    }
  }
}
