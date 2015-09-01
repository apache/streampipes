package de.fzi.cep.sepa.rest.v2;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.v2.Notification;
import de.fzi.cep.sepa.storage.controller.StorageManager;

@Path("/v2/users/{username}/notifications")
public class NotificationImpl extends AbstractRestInterface implements Notification {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getNotifications() {
		return toJson(StorageManager.INSTANCE.getNotificationStorageApi().getAllNotifications());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/unread")
	@Override
	public String getUnreadNotifications() {
		return toJson(StorageManager.INSTANCE.getNotificationStorageApi().getUnreadNotifications());
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{notificationId}")
	@Override
	public String deleteNotification(@PathParam("notificationId") String notificationId) {
		boolean success = StorageManager.INSTANCE.getNotificationStorageApi().deleteNotification(notificationId);
		if (success) return toJson(Notifications.success("Notification deleted"));
		else return toJson(Notifications.error("Could not delete notification"));
		
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{notificationId}")
	@Override
	public String modifyNotificationStatus(@PathParam("notificationId") String notificationId) {
		boolean success = StorageManager.INSTANCE.getNotificationStorageApi().changeNotificationStatus(notificationId);
		if (success) return toJson(Notifications.success("Ok"));
		else return toJson(Notifications.error("Error"));
	}

	

}
