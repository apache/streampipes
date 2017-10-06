package org.streampipes.app.file.export.api;

import javax.ws.rs.core.Response;

public interface IElasticsearch {

    Response createFiles();

    Response getFile(String fileId);


}
