package org.streampipes.pe.sinks.standalone.samples.couchdb;

import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class CouchDbParameters extends EventSinkBindingParams {

  private String couchDbHost;
  private Integer couchDbPort;
  private String databaseName;

  public CouchDbParameters(SecInvocation graph, String couchDbHost, Integer couchDbPort, String databaseName) {
    super(graph);
    this.couchDbHost = couchDbHost;
    this.couchDbPort = couchDbPort;
    this.databaseName = databaseName;
  }

  public String getCouchDbHost() {
    return couchDbHost;
  }

  public Integer getCouchDbPort() {
    return couchDbPort;
  }

  public String getDatabaseName() {
    return databaseName;
  }
}
