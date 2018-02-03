package org.streampipes.rest.impl;

import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.INotification;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/notifications")
public class Notification extends AbstractRestInterface implements INotification {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getNotifications() {
        return ok(getNotificationStorage()
                .getAllNotifications());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/unread")
    @Override
    public Response getUnreadNotifications() {
        return ok(getNotificationStorage()
                .getUnreadNotifications());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{notificationId}")
    @Override
    public Response deleteNotification(@PathParam("notificationId") String notificationId) {
        boolean success = getNotificationStorage()
                .deleteNotification(notificationId);
        if (success) {
            return ok(Notifications.success("Notification deleted"));
        } else {
            return ok(Notifications.error("Could not delete notification"));
        }

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{notificationId}")
    @Override
    public Response modifyNotificationStatus(@PathParam("notificationId") String notificationId) {
        boolean success = getNotificationStorage()
                .changeNotificationStatus(notificationId);
        if (success) {
            return ok(Notifications.success("Ok"));
        } else {
            return ok(Notifications.error("Error"));
        }
    }
}
