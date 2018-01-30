package org.streampipes.rest.impl;

import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.api.IUser;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{email}")
public class User extends AbstractRestInterface implements IUser {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getUserDetails(@PathParam("email") String email) {
        org.streampipes.model.client.user.User user = getUser(email);
        user.setPassword("");

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
            org.streampipes.model.client.user.User existingUser = getUser(user.getEmail());
            user.setPassword(existingUser.getPassword());
            getUserStorage().updateUser(user);
            return ok(Notifications.success("User updated"));
        } else {
            return statusMessage(Notifications.error("User not found"));
        }
    }

    private org.streampipes.model.client.user.User getUser(String email) {
        return getUserStorage().getUser(email);
    }
}
