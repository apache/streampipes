package de.fzi.cep.sepa.manager.validator;

import java.util.List;

public interface Validator<T> {

	public boolean validate(List<T> left, List<T> right);
	
	public boolean validate(List<T> firstLeft, List<T> secondLeft, List<T> firstRight, List<T> secondRight);
}
