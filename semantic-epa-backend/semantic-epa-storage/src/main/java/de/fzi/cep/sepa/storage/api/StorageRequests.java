package de.fzi.cep.sepa.storage.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;

public interface StorageRequests {

	public boolean storeSEP(SEP sep);
	
	public boolean storeSEP(String jsonld);
	
	public boolean storeSEPA(SEPA sepa);
	
	public boolean storeSEPA(String jsonld);
	
	public SEP getSEPById(URI rdfId);
	
	public SEP getSEPById(String rdfId) throws URISyntaxException;
	
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

	
}
