package de.fzi.cep.sepa.storage.util;

import java.util.List;

public abstract interface Converter<S,T>  {

	public S fromClientModel(T clientModel);
	
	public T toClientModel(S serverModel);
	
	public List<S> fromClientModel(List<T> clientModel);
	
	public List<T> toClientModel(List<S> serverModel);
	
}
