package org.streampipes.rest.impl;

import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.api.IUser;
import org.streampipes.storage.controller.StorageManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by riemer on 01.11.2016.
 */
@Path("/v2/users/{username}")
public class User extends AbstractRestInterface implements IUser {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getUserDetails(@PathParam("username") String email) {
        org.streampipes.model.client.user.User user = StorageManager.INSTANCE.getUserStorageAPI().getUser(email);

        if (user != null) {
            return ok(user);
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateUserDetails(org.streampipes.model.client.user.User user) {
        if (user != null) {
            StorageManager.INSTANCE.getUserStorageAPI().updateUser(user);
            return ok(Notifications.success("User updated"));
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }
}
