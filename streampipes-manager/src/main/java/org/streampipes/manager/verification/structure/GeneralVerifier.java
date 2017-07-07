package org.streampipes.manager.verification.structure;

import java.util.List;

import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.NamedSEPAElement;

public class GeneralVerifier<T extends NamedSEPAElement> extends AbstractVerifier {

	private T description;
	
	public GeneralVerifier(T description)
	{
		this.description = description;
	}
	
	@Override
	public List<VerificationResult> validate() {
		if (description.getIconUrl() == null) addWarning(NotificationType.WARNING_NO_ICON);
		if (description.getName() == null) addWarning(NotificationType.WARNING_NO_NAME);
		
		return validationResults;
	}

}
