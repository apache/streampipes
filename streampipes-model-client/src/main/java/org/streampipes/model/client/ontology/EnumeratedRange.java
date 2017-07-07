package org.streampipes.model.client.ontology;

import java.util.List;

public class EnumeratedRange extends Range {

	private static final String TITLE = "";
	private static final String DESCRIPTION = "";
	
	private List<EnumeratedValue> enumeratedValues;

	public EnumeratedRange(List<EnumeratedValue> enumeratedValues) {
		super(RangeType.ENUMERATION, TITLE, DESCRIPTION);
		this.enumeratedValues = enumeratedValues;
	}

	public List<EnumeratedValue> getEnumeratedValues() {
		return enumeratedValues;
	}

	public void setEnumeratedValues(List<EnumeratedValue> enumeratedValues) {
		this.enumeratedValues = enumeratedValues;
	}
	
		
}
