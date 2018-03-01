package org.streampipes.pe.sinks.standalone.samples.couchdb;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.util.Logger;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class CouchDb implements EventSink<CouchDbParameters> {

  private CouchDbClient couchDbClient;

  private static Logger LOG;

  @Override
  public void bind(CouchDbParameters parameters) throws SpRuntimeException {
      LOG = parameters.getGraph().getLogger(CouchDb.class);
      this.couchDbClient = new CouchDbClient(new CouchDbProperties(
            parameters.getDatabaseName(),
            true,
            "http",
            parameters.getCouchDbHost(),
            parameters.getCouchDbPort(),
            parameters.getUser(),
            parameters.getPassword()
      ));
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
      couchDbClient.save(event);
      LOG.info("Saved event: " + event.toString());
  }

  @Override
  public void discard() throws SpRuntimeException {
  }


}
