package de.fzi.cep.sepa.validation;

import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public class Validation<B extends BindingParameters> {

	private final boolean isBindable;

	private final RuntimeParameters<B> properties;

	private final ValidationResult result;

	public Validation(boolean isBindable, RuntimeParameters<B> properties, ValidationResult result) {
		this.isBindable = isBindable;
		this.properties = properties;
		this.result = result;
	}

	public boolean isBindable() {
		return isBindable;
	}

	public RuntimeParameters<B> getParameters() {
		return properties;
	}

	public ValidationResult getResult() {
		return result;
	}
}
