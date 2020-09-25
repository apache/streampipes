package org.apache.streampipes.rest.impl;

import org.apache.streampipes.model.labeling.Label;
import org.apache.streampipes.rest.api.ILabel;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/labels")
public class LabelResource extends AbstractRestInterface implements ILabel {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response getAllLabels() {
        return ok(StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getLabelStorageAPI()
                .getAllLabels()
        );
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response addLabel(Label label) {
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getLabelStorageAPI()
                .storeLabel(label);
        return ok();
    }

//    @GET
//    @Path("{labelId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateLabel(@PathParam("labelId") String labelId, Label label) {
//        StorageDispatcher.INSTANCE
//                .getNoSqlStore()
//                .getLabelStorageAPI()
//                .updateLabel(label);
//
//        return ok();
//    }

}
