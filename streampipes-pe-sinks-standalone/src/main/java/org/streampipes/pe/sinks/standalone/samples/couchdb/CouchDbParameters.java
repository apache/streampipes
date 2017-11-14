package org.streampipes.pe.sinks.standalone.samples.couchdb;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class CouchDbParameters extends EventSinkBindingParams {

  private String couchDbHost;
  private Integer couchDbPort;
  private String databaseName;
  private String user;
  private String password;

  public CouchDbParameters(DataSinkInvocation graph, String couchDbHost, Integer couchDbPort, String databaseName, String user, String password) {
    super(graph);
    this.couchDbHost = couchDbHost;
    this.couchDbPort = couchDbPort;
    this.databaseName = databaseName;
    this.user = user;
    this.password = password;
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

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
