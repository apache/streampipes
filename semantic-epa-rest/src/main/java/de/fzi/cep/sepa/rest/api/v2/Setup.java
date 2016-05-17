package de.fzi.cep.sepa.rest.api.v2;

public interface Setup {

	public String isConfigured();

	public String configure(String json);

	public String getConfiguration();

	public String updateConfiguration(String json);
}
