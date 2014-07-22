package de.fzi.cep.sepa.validation;

import de.fzi.cep.sepa.desc.AgentConfiguration;
import de.fzi.cep.sepa.desc.AgentDescription;

public interface Validator {

	Validation<?> validate(AgentConfiguration config, AgentDescription description);
}
