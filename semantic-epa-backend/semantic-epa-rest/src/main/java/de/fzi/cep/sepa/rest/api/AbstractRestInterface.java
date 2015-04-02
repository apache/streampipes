package de.fzi.cep.sepa.rest.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFHandlerException;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.messages.ErrorMessage;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.SuccessMessage;
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
	
	protected <T> String toJsonLd(T object)
	{
		try {
			return de.fzi.cep.sepa.commons.Utils.asString(Transformer.generateCompleteGraph(new GraphImpl(), object));
		} catch (RDFHandlerException | IllegalArgumentException
				| IllegalAccessException | SecurityException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
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
	
	protected String constructErrorMessage(Notification... notifications)
	{
		return constructMessage(new ErrorMessage(notifications));
	}
	
	
	private String constructMessage(Message message)
	{
		return toJson(message);
	}
	
}
