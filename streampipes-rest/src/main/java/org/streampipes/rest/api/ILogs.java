package org.streampipes.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface ILogs {

    Response getAllByPipelineId(String pipelineId);

    Response getAllByPeuri(String peuri);

}
