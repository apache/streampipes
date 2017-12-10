package org.streampipes.app.file.export.api;

import org.streampipes.app.file.export.ElasticsearchAppData;

import javax.ws.rs.core.Response;

public interface IElasticsearch {

    Response createFiles(ElasticsearchAppData data);

    Response getFile(String fileName);

    Response deleteFile(String fileName);

    Response getEndpoints();


}
