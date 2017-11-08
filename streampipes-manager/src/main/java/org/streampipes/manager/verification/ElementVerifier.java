package org.streampipes.manager.verification;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.manager.verification.messages.VerificationError;
import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.manager.verification.structure.GeneralVerifier;
import org.streampipes.manager.verification.structure.Verifier;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.client.messages.ErrorMessage;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.messages.SuccessMessage;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.storage.api.StorageRequests;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.storage.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class ElementVerifier<T extends NamedSEPAElement> {

	protected static final Logger logger = Logger.getAnonymousLogger();
	
	private String graphData;
	private Class<T> elementClass;
	protected T elementDescription;
	
	protected List<VerificationResult> validationResults;
	protected List<Verifier> validators;
	
	protected StorageRequests storageApi = StorageManager.INSTANCE.getStorageAPI();
	protected UserService userService = StorageManager.INSTANCE.getUserService();
	
	public ElementVerifier(String graphData, Class<T> elementClass)
	{
		this.elementClass = elementClass;
		this.graphData = graphData;
		this.validators = new ArrayList<>();
		this.validationResults = new ArrayList<>();
	}
	
	protected void collectValidators()
	{
		validators.add(new GeneralVerifier<T>(elementDescription));
	}
	
	protected abstract StorageState store(String username, boolean publicElement);
	
	protected abstract void update(String username);
	
	protected void verify()
	{
		collectValidators();
		validators.forEach(validator -> validationResults.addAll(validator.validate()));
	}
	
	public Message verifyAndAdd(String username, boolean publicElement) throws SepaParseException
	{
		try {
			this.elementDescription = transform();
		} catch (RDFParseException | UnsupportedRDFormatException
				| RepositoryException | IOException e) {
			return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
		}
		verify();
		if (isVerifiedSuccessfully())
		{
			StorageState state = store(username, publicElement);
			if (state == StorageState.STORED) return successMessage();
			else if (state == StorageState.ALREADY_IN_SESAME) return addedToUserSuccessMessage();
			else return skippedSuccessMessage();
		}
		else return errorMessage();
		
	}
	
	public Message verifyAndUpdate(String username) throws SepaParseException
	{
		try {
			this.elementDescription = transform();
		} catch (RDFParseException | UnsupportedRDFormatException
				| RepositoryException | IOException e) {
			return new ErrorMessage(NotificationType.UNKNOWN_ERROR.uiNotification());
		}
		verify();
		if (isVerifiedSuccessfully())
		{
			update(username);
			return successMessage();
		}
		else return errorMessage();
		
	}
	
	private Message errorMessage() {
		return new ErrorMessage(elementDescription.getName(), collectNotifications());
	}
	
	private Message successMessage() {
		List<Notification> notifications = collectNotifications();
		notifications.add(NotificationType.STORAGE_SUCCESS.uiNotification());
		return new SuccessMessage(elementDescription.getName(), notifications);
	}
	
	private Message skippedSuccessMessage() {
		List<Notification> notifications = collectNotifications();
		notifications.add(new Notification("Already exists", "This element is already in your list of elements, skipped."));
		return new SuccessMessage(elementDescription.getName(), notifications);
	}
	
	private Message addedToUserSuccessMessage() {
		List<Notification> notifications = collectNotifications();
		notifications.add(new Notification("Already stored", "Element description already stored, added element to user"));
		return new SuccessMessage(elementDescription.getName(), notifications);
	}
	
	private List<Notification> collectNotifications()
	{
		List<Notification> notifications = new ArrayList<>();
		validationResults.forEach(vr -> notifications.add(vr.getNotification()));
		return notifications;
	}

	private boolean isVerifiedSuccessfully()
	{
		return !validationResults.stream().anyMatch(validator -> (validator instanceof VerificationError));
	}
	
	protected T transform() throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
	{
		return new JsonLdTransformer().fromJsonLd(graphData, elementClass);
	}
}
