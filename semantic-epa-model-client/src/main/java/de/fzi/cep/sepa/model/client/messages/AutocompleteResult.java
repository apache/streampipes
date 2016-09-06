package de.fzi.cep.sepa.model.client.messages;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteResult {

	List<AutocompleteItem> result;
	
	public AutocompleteResult(List<AutocompleteItem> result)
	{
		this.result = result;
	}
	
	public AutocompleteResult() 
	{
		this.result = new ArrayList<>();
	}

	public List<AutocompleteItem> getResult() {
		return result;
	}

	public void setResult(List<AutocompleteItem> result) {
		this.result = result;
	}
	
	public void add(AutocompleteItem item)
	{
		this.result.add(item);
	}
	
}
