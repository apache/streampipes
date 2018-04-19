package org.streampipes.rest.api;

import javax.ws.rs.core.Response;

public interface ICouchdb {

    Response getAllData(String table);

}
