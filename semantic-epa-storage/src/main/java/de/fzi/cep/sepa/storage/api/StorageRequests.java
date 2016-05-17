package de.fzi.cep.sepa.storage.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;

public interface StorageRequests {
	
	public boolean storeInvocableSEPAElement(InvocableSEPAElement element);

	public boolean storeSEP(SepDescription sep);
	
	public boolean storeSEP(String jsonld);
	
	public boolean storeSEPA(SepaDescription sepa);
	
	public boolean storeSEPA(String jsonld);
	
	public SepDescription getSEPById(URI rdfId);
	
	public SepDescription getSEPById(String rdfId) throws URISyntaxException;
	
	public SepaDescription getSEPAById(String rdfId) throws URISyntaxException;
	
	public SepaDescription getSEPAById(URI rdfId);
	
	public SecDescription getSECById(String rdfId) throws URISyntaxException;
	
	public SecDescription getSECById(URI rdfId);
	
	public List<SepDescription> getAllSEPs();
	
	public List<SepaDescription> getAllSEPAs();
	
	public List<SepDescription> getSEPsByDomain(String domain);
	
	public List<SepaDescription> getSEPAsByDomain(String domain);
	
	public boolean deleteSEP(SepDescription sep);
	
	public boolean deleteSEP(String rdfId);
	
	public boolean deleteSEPA(SepaDescription sepa);
	
	public boolean deleteSEPA(String rdfId);
	
	public boolean exists(SepDescription sep);
	
	public boolean exists(SepaDescription sepa);
	
	public boolean update(SepDescription sep);
	
	public boolean update(SepaDescription sepa);

	public boolean exists(SecDescription sec);

	public boolean update(SecDescription sec);

	public boolean deleteSEC(SecDescription sec);
	
	public boolean storeSEC(SecDescription sec);

	public List<SecDescription> getAllSECs();
	
	public StaticProperty getStaticPropertyById(String rdfId);
	
	public EventStream getEventStreamById(String rdfId);
	
}
