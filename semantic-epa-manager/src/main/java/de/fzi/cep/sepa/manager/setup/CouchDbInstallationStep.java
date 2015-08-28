package de.fzi.cep.sepa.manager.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lightcouch.DesignDocument;
import org.lightcouch.DesignDocument.MapReduce;
import org.lightcouch.Response;

import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.storage.util.Utils;

public class CouchDbInstallationStep implements InstallationStep {


	public CouchDbInstallationStep() {

	}

	@Override
	public List<Message> install() {
		List<Message> msgs = new ArrayList<>();
		msgs.addAll(createDatabases());
		msgs.addAll(createViews());
		return msgs;
	}
	
	private List<Message> createDatabases()
	{
		try {
			Utils.getCouchDbUserClient();
			Utils.getCouchDbMonitoringClient();
			Utils.getCouchDbPipelineClient();
			Utils.getCouchDbConnectionClient();
			return Arrays.asList(Notifications.success("Creating CouchDB databases..."));
		} catch (Exception e)
		{
			return Arrays.asList(Notifications.error("Creating CouchDB databases..."));
		}
	}
	
	private List<Message> createViews()
	{
		List<Message> result = new ArrayList<>();
		result.add(addUserView());
		result.add(addConnectionView());
		return result;
	}
	
	private Message addUserView()
	{
		try {
			DesignDocument userDocument = prepareDocument("_design/users");
			Map<String, MapReduce> views = new HashMap<>();
			
			MapReduce passwordFunction = new MapReduce();
			passwordFunction.setMap("function(doc) { if(doc.email&& doc.password) { emit(doc.email, doc.password); } }");
			
			MapReduce usernameFunction = new MapReduce();
			usernameFunction.setMap("function(doc) { if(doc.email) { emit(doc.email, doc); } }");
			
			views.put("password", passwordFunction);
			views.put("username", usernameFunction);
			
			userDocument.setViews(views);
			Response resp = Utils.getCouchDbUserClient().design().synchronizeWithDb(userDocument);
		
			if (resp.getError() != null) return Notifications.error("Preparing database 'users'...");
			else return Notifications.success("Preparing database 'users'...");
		} catch (Exception e)
		{
			return Notifications.error("Preparing database 'users'...");
		}
	}
	
	private Message addConnectionView()
	{
		try {
			DesignDocument connectionDocument = prepareDocument("_design/connection");
			Map<String, MapReduce> views = new HashMap<>();
			
			MapReduce frequentFunction = new MapReduce();
			frequentFunction.setMap("function(doc) { if(doc.from && doc.to) { emit([doc.from, doc.to] , 1 ); } }");
			frequentFunction.setReduce("function (key, values) { return sum(values); }");
			
			views.put("frequent", frequentFunction);
			
			connectionDocument.setViews(views);
			Response resp = Utils.getCouchDbConnectionClient().design().synchronizeWithDb(connectionDocument);
			
			if (resp.getError() != null) return Notifications.error("Preparing database 'connection'...");
			else return Notifications.success("Preparing database 'connection'...");
		} catch (Exception e)
		{
			return Notifications.error("Preparing database 'connection'...");
		}
	}
	
	private DesignDocument prepareDocument(String id)
	{
		DesignDocument doc = new DesignDocument();
		doc.setLanguage("javascript");
		doc.setId(id);
		return doc;
	}
}
