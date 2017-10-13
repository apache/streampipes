package org.streampipes.app.file.export.api;

import javax.ws.rs.core.Response;

public interface IElasticsearch {

    Response createFiles(String index, long timestampFrom, long timeStampTo);

    Response getFile(String fileName);

    Response deleteFile(String fileName);

    Response getEndpoints();


}
