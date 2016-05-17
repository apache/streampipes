package de.fzi.cep.sepa.storage.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.messages.ProaSenseNotificationMessage;
import de.fzi.cep.sepa.storage.api.NotificationStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class NotificationStorageImpl implements NotificationStorage {

	Logger LOG = LoggerFactory.getLogger(NotificationStorageImpl.class);
	
	@Override
	public ProaSenseNotificationMessage getNotification(String notificationId) {
		CouchDbClient dbClient = Utils.getCouchDbNotificationClient();
        try {
           ProaSenseNotificationMessage msg = dbClient.find(ProaSenseNotificationMessage.class, notificationId);
            dbClient.shutdown();
            return msg;
        } catch (NoDocumentException e) {
			LOG.error("No pipeline wit ID %s found", notificationId);
			dbClient.shutdown();
            return null;
        }
	}

	@Override
	public List<ProaSenseNotificationMessage> getAllNotifications() {
		 CouchDbClient dbClient = Utils.getCouchDbNotificationClient();
    	 List<ProaSenseNotificationMessage> msgs = dbClient.view("_all_docs")
    			  .includeDocs(true)
    			  .query(ProaSenseNotificationMessage.class);
    	 dbClient.shutdown();
    	 return msgs;
	}

	@Override
	public boolean addNotification(ProaSenseNotificationMessage notification) {
		CouchDbClient dbClient = Utils.getCouchDbNotificationClient();
        dbClient.save(notification);
      
        dbClient.shutdown();
        return true;
	}

	@Override
	public boolean changeNotificationStatus(String notificationId) {
		ProaSenseNotificationMessage msg = getNotification(notificationId);
		msg.setRead(!msg.isRead());
		
		CouchDbClient dbClient = Utils.getCouchDbNotificationClient();
        dbClient.update(msg);
        dbClient.shutdown();
		return true;
	}

	@Override
	public boolean deleteNotification(String notificationId) {
		CouchDbClient dbClientPipeline = Utils.getCouchDbNotificationClient();
        try {
            ProaSenseNotificationMessage removeMsg = dbClientPipeline.find(ProaSenseNotificationMessage.class, notificationId);
            dbClientPipeline.remove(removeMsg);
            dbClientPipeline.shutdown();
            return true;
        } catch (NoDocumentException e) {
        	dbClientPipeline.shutdown();
            e.printStackTrace();
        }
        return false;
	}

	@Override
	public List<ProaSenseNotificationMessage> getUnreadNotifications() {
		List<ProaSenseNotificationMessage> msgs = getAllNotifications();
		
		return msgs.stream().filter(m -> !m.isRead()).collect(Collectors.toList());
	}

}
