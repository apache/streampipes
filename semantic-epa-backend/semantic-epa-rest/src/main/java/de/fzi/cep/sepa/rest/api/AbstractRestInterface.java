package de.fzi.cep.sepa.rest.api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import de.fzi.cep.sepa.storage.impl.UserStorage;

import org.apache.http.client.ClientProtocolException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.messages.ErrorMessage;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.SuccessMessage;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.service.UserService;
import de.fzi.cep.sepa.storage.util.Transformer;
import de.fzi.sepa.model.client.util.Utils;

public abstract class AbstractRestInterface {

	protected static StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	protected static PipelineStorage pipelineStorage = StorageManager.INSTANCE.getPipelineStorageAPI();
	protected static UserStorage userStorage = StorageManager.INSTANCE.getUserStorageAPI();	
	protected static UserService userService = StorageManager.INSTANCE.getUserService();
	
	public static void reloadApis()
	{
		requestor = StorageManager.INSTANCE.getStorageAPI();
		pipelineStorage = StorageManager.INSTANCE.getPipelineStorageAPI();
		userStorage = StorageManager.INSTANCE.getUserStorageAPI();
		userService = StorageManager.INSTANCE.getUserService();
	}
	
	protected <T> T fromJson(String payload, Class<T> clazz)
	{
		return Utils.getGson().fromJson(payload, clazz);
	}
	
	protected <T> String toJson(T object)
	{
		return Utils.getGson().toJson(object);
	}
	
	protected <T> String toJsonLd(T object)
	{
		try {
			return de.fzi.cep.sepa.commons.Utils.asString(new JsonLdTransformer().toJsonLd(object));
		} catch (RDFHandlerException | IllegalArgumentException
				| IllegalAccessException | SecurityException | InvocationTargetException | ClassNotFoundException | InvalidRdfException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	
	protected String parseURIContent(String payload) throws URISyntaxException, ClientProtocolException, IOException
	{
		return parseURIContent(payload, null);
	}
	
	protected String parseURIContent(String payload, String mediaType) throws URISyntaxException, ClientProtocolException, IOException
	{
		URI uri = new URI(payload);
		return HttpJsonParser.getContentFromUrl(uri, mediaType);
	}
	
	protected <T extends NamedSEPAElement> T parseObjectContent(Class<T> clazz, String payload) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
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

	protected String getCurrentUsername() throws AuthenticationException {
		if (SecurityUtils.getSubject().isAuthenticated()) {
			return SecurityUtils.getSubject().getPrincipal().toString();
		}
		throw new AuthenticationException("Not authenticated");
	}
	
	protected boolean authorized(String username)
	{
		return username.equals(SecurityUtils.getSubject().getPrincipal().toString());
	}
	
	protected boolean isAuthenticated() {
		return SecurityUtils.getSubject().isAuthenticated();
	}
	
	protected String decode(String encodedString) {
		return URLDecoder.decode(encodedString);
	}
	
}
