package org.apache.streampipes.rest.impl;


import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.rest.api.ICategory;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.management.StorageDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/labeling/category")
public class CategoryResource extends AbstractRestInterface implements ICategory {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response getAll() {
        return ok(StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getCategoryStorageAPI()
                .getAllCategories()
        );
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response add(Category category) {
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getCategoryStorageAPI()
                .storeCategory(category);
        return ok();
    }

    @POST
    @Path("/delete/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response delete(@PathParam("categoryId") String key) {
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getCategoryStorageAPI()
                .deleteCategory(key);
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getLabelStorageAPI()
                .deleteAllForCategory(key);
        return ok();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JacksonSerialized
    @Override
    public Response update(Category category) {
        StorageDispatcher.INSTANCE
                .getNoSqlStore()
                .getCategoryStorageAPI()
                .updateCategory(category);
        return ok();
    }
}
