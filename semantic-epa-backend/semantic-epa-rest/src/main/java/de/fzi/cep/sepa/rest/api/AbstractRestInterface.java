package de.fzi.cep.sepa.rest.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.rest.messages.ErrorMessage;
import de.fzi.cep.sepa.rest.messages.Message;
import de.fzi.cep.sepa.rest.messages.Notification;
import de.fzi.cep.sepa.rest.messages.SuccessMessage;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.Transformer;
import de.fzi.sepa.model.client.util.Utils;

public abstract class AbstractRestInterface {

	protected static StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	protected static PipelineStorage pipelineStorage = StorageManager.INSTANCE.getPipelineStorageAPI();
	
	protected <T> String toJson(T object)
	{
		return Utils.getGson().toJson(object);
	}
	
	protected String parseURIContent(String payload) throws URISyntaxException, ClientProtocolException, IOException
	{
		URI uri = new URI(payload);
		return HttpJsonParser.getContentFromUrl(uri);
	}
	
	protected <T extends NamedSEPAElement> T parseObjectContent(Class<T> clazz, String payload)
	{
		return Transformer.fromJsonLd(clazz, payload);
	}
	
	protected String constructSuccessMessage(Notification... notifications)
	{
		return constructMessage(new SuccessMessage(notifications));
	}
	
	protected String constructErrorMessage(Exception e, Notification... notifications)
	{
		return constructMessage(e, new ErrorMessage(notifications));
	}
	
	private String constructMessage(Exception e, Message message)
	{
		message.addNotification(new Notification("Type: ", e.getClass().getCanonicalName()));
		return constructMessage(message);
	}
	
	private String constructMessage(Message message)
	{
		return toJson(message);
	}
	
}
