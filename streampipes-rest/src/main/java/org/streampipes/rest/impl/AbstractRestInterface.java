/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl;

import org.apache.http.client.ClientProtocolException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.manager.storage.UserManagementService;
import org.streampipes.manager.storage.UserService;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.client.messages.ErrorMessage;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.SuccessMessage;
import org.streampipes.rest.http.HttpJsonParser;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.api.INoSqlStorage;
import org.streampipes.storage.api.INotificationStorage;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.api.IPipelineStorage;
import org.streampipes.storage.api.ITripleStorage;
import org.streampipes.storage.api.IUserStorage;
import org.streampipes.storage.api.IVisualizationStorage;
import org.streampipes.storage.management.StorageDispatcher;
import org.streampipes.storage.management.StorageManager;
import org.streampipes.storage.rdf4j.util.Transformer;

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

	protected IPipelineElementDescriptionStorage getPipelineElementRdfStorage() {
		return StorageManager.INSTANCE.getStorageAPI();
	}

	protected IPipelineStorage getPipelineStorage() {
		return getNoSqlStorage().getPipelineStorageAPI();
	}

	protected IUserStorage getUserStorage() {
		return getNoSqlStorage().getUserStorageAPI();
	}

	protected UserService getUserService() {
		return UserManagementService.getUserService();
	}

	protected IVisualizationStorage getVisualizationStorage() {
		return getNoSqlStorage().getVisualizationStorageApi();
	}

	protected INotificationStorage getNotificationStorage() {
		return getNoSqlStorage().getNotificationStorageApi();
	}

	protected INoSqlStorage getNoSqlStorage() {
		return StorageDispatcher.INSTANCE.getNoSqlStore();
	}

	protected ITripleStorage getTripleStorage() {
		return StorageDispatcher.INSTANCE.getTripleStore();
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
	
	protected <T extends NamedStreamPipesEntity> T parseObjectContent(Class<T> clazz, String payload) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
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
