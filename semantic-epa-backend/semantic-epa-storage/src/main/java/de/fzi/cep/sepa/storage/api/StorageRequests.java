package de.fzi.cep.sepa.storage.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;

public interface StorageRequests {

	public boolean storeSEP(SEP sep);
	
	public boolean storeSEP(String jsonld);
	
	public boolean storeSEPA(SEPA sepa);
	
	public boolean storeSEPA(String jsonld);
	
	public SEP getSEPById(URI rdfId);
	
	public SEP getSEPById(String rdfId) throws URISyntaxException;
	
	public SEPA getSEPAById(String rdfId) throws URISyntaxException;
	
	public SEPA getSEPAById(URI rdfId);
	
	public SEC getSECById(String rdfId) throws URISyntaxException;
	
	public SEC getSECById(URI rdfId);
	
	public List<SEP> getAllSEPs();
	
	public List<SEPA> getAllSEPAs();
	
	public List<SEP> getSEPsByDomain(String domain);
	
	public List<SEPA> getSEPAsByDomain(String domain);
	
	public boolean deleteSEP(SEP sep);
	
	public boolean deleteSEP(String rdfId);
	
	public boolean deleteSEPA(SEPA sepa);
	
	public boolean deleteSEPA(String rdfId);
	
	public boolean exists(SEP sep);
	
	public boolean exists(SEPA sepa);
	
	public boolean update(SEP sep);
	
	public boolean update(SEPA sepa);

	public boolean exists(SEC sec);

	public boolean update(SEC sec);

	public boolean deleteSEC(SEC sec);
	
	public boolean storeSEC(SEC sec);

	public List<SEC> getAllSECs();
	
	public StaticProperty getStaticPropertyById(String rdfId);
	
}
