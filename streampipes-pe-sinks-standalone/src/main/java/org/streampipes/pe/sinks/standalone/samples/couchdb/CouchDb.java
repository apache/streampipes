package org.streampipes.pe.sinks.standalone.samples.couchdb;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class CouchDb implements EventSink<CouchDbParameters> {

  @Override
  public void bind(CouchDbParameters parameters) throws SpRuntimeException {

  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {

  }

  @Override
  public void discard() throws SpRuntimeException {

  }
}
