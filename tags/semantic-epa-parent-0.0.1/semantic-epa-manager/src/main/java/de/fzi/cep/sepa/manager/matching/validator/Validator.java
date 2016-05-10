package de.fzi.cep.sepa.manager.matching.validator;

public interface Validator<T> {

	public boolean validate(T left, T right);
	
	public boolean validate(T firstLeft, T secondLeft, T firstRight, T secondRight);
}
