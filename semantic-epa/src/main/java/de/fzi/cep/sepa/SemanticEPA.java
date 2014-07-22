package de.fzi.cep.sepa;

import java.util.Optional;

import org.apache.camel.CamelContext;

import de.fzi.cep.sepa.desc.AgentConfiguration;
import de.fzi.cep.sepa.desc.AgentDescription;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.validation.Validation;
import de.fzi.cep.sepa.validation.ValidationResult;
import de.fzi.cep.sepa.validation.Validator;

public class SemanticEPA {

	private final AgentDescription description;

	private final Validator validator;

	private final CamelContext context;

	private Optional<EPRuntime> runtime;

	public SemanticEPA(AgentDescription description, Validator validator, CamelContext context) {
		this.description = description;
		this.validator = validator;
		this.context = context;
		this.runtime = Optional.empty();
	}

	public AgentDescription getDescription() {
		return description;
	}

	public ValidationResult bind(AgentConfiguration config) {
		Validation<?> validation = validator.validate(config, description);
		if (validation.isBindable()) {
			runtime.ifPresent(EPRuntime::discard);
			runtime = Optional.of(new EPRuntime(context, validation.getParameters()));
		}
		return validation.getResult();
	}

	public void unbind() {
		runtime.ifPresent(EPRuntime::discard);
		runtime = Optional.empty();
	}
}
