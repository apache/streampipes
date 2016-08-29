package de.fzi.cep.sepa.rest.api;

public interface ISetup {

	String isConfigured();

	String configure(String json);

	String getConfiguration();

	String updateConfiguration(String json);
}
