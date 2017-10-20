package org.streampipes.rest.impl;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import org.apache.http.client.ClientProtocolException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.commons.Utils;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.client.messages.ErrorMessage;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.SuccessMessage;
import org.streampipes.model.transform.JsonLdTransformer;
import org.streampipes.model.util.GsonSerializer;
import org.streampipes.rest.http.HttpJsonParser;
import org.streampipes.storage.api.PipelineStorage;
import org.streampipes.storage.api.StorageRequests;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.storage.impl.UserStorage;
import org.streampipes.storage.service.UserService;
import org.streampipes.storage.util.Transformer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import javax.ws.rs.core.Response;

public abstract class AbstractRestInterface {

	protected <T> String toJsonLd(T object)
	{
		try {
			return Utils.asString(new JsonLdTransformer().toJsonLd(object));
		} catch (RDFHandlerException | IllegalArgumentException
				| IllegalAccessException | SecurityException | InvocationTargetException | ClassNotFoundException | InvalidRdfException e) {
			return toJson(constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage())));
		}
	}

	protected StorageRequests getPipelineElementRdfStorage() {
		return StorageManager.INSTANCE.getStorageAPI();
	}

	protected PipelineStorage getPipelineStorage() {
		return StorageManager.INSTANCE.getPipelineStorageAPI();
	}

	protected UserStorage getUserStorage() {
		return StorageManager.INSTANCE.getUserStorageAPI();
	}

	protected UserService getUserService() {
		return StorageManager.INSTANCE.getUserService();
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
	
	protected Response constructSuccessMessage(Notification... notifications)
	{
		return statusMessage(new SuccessMessage(notifications));
	}
	
	protected Response constructErrorMessage(Notification... notifications)
	{
		return statusMessage(new ErrorMessage(notifications));
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
	
	@SuppressWarnings("deprecation")
	protected String decode(String encodedString) {
		return URLDecoder.decode(encodedString);
	}

	protected Response statusMessage(Message message) {
		return Response
				.ok()
				.entity(message)
				.build();
	}

	protected <T> Response ok(T entity) {
		return Response
				.ok(entity)
				.build();
	}

	protected <T> String toJson(T element) {
		return GsonSerializer.getGson().toJson(element);
	}
	
}
