package de.fzi.cep.sepa.manager.verification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.manager.verification.messages.VerificationError;
import de.fzi.cep.sepa.manager.verification.messages.VerificationResult;
import de.fzi.cep.sepa.manager.verification.structure.GeneralVerifier;
import de.fzi.cep.sepa.manager.verification.structure.Verifier;
import de.fzi.cep.sepa.messages.ErrorMessage;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.SuccessMessage;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.service.UserService;

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
	
	protected abstract void store(String username, boolean publicElement);
	
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
			store(username, publicElement);
			return successMessage();
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
		return new ErrorMessage(collectNotifications());
	}
	
	private Message successMessage() {
		List<Notification> notifications = collectNotifications();
		notifications.add(NotificationType.STORAGE_SUCCESS.uiNotification());
		return new SuccessMessage(notifications);
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
