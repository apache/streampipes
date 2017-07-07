package de.fzi.cep.sepa.esper.output.topx;

public enum OrderDirection {
ASCENDING(" asc"), DESCENDING(" desc");
	
	String epl;

	OrderDirection(String epl)
	{
		this.epl = epl;
	}
	
	public String toEpl()
	{
		return epl;
	}
}
