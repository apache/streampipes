package org.streampipes.dataformat.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.model.vocabulary.MessageFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonDataFormatDefinition implements SpDataFormatDefinition<String> {

  private Logger LOG = L

  private ObjectMapper objectMapper;

  public JsonDataFormatDefinition() {
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public String getTransportFormatRdfUri() {
    return MessageFormat.Json;
  }

  @Override
  public String fromBytesArray(byte[] event) {
    return new String(event);
  }

  @Override
  public Map<String, Object> toMap(String event) {
    try {
      return objectMapper.readValue(event, HashMap.class);
    } catch (IOException e) {
      throw new
    }
  }

  @Override
  public String fromMap(Map<String, Object> event) {
    return null;
  }
}
