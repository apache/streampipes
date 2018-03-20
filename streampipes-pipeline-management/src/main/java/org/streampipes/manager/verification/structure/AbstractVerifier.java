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

package org.streampipes.manager.verification.structure;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.manager.verification.messages.VerificationError;
import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.manager.verification.messages.VerificationWarning;
import org.streampipes.model.client.messages.NotificationType;

public abstract class AbstractVerifier implements Verifier {

	protected List<VerificationResult> validationResults;
	
	public AbstractVerifier()
	{
		this.validationResults = new ArrayList<>();
	}
	
	protected void addWarning(NotificationType notificationType)
	{
		validationResults.add(new VerificationWarning(notificationType));
	}
	
	protected void addError(NotificationType notificationType)
	{
		validationResults.add(new VerificationError(notificationType));
	}
}
