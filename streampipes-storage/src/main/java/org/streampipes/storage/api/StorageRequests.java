package org.streampipes.storage.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.staticproperty.StaticProperty;

public interface StorageRequests {
	
	boolean storeInvocableSEPAElement(InvocableSEPAElement element);

	boolean storeSEP(SepDescription sep);
	
	boolean storeSEP(String jsonld);
	
	boolean storeSEPA(SepaDescription sepa);
	
	boolean storeSEPA(String jsonld);
	
	SepDescription getSEPById(URI rdfId);
	
	SepDescription getSEPById(String rdfId) throws URISyntaxException;
	
	SepaDescription getSEPAById(String rdfId) throws URISyntaxException;
	
	SepaDescription getSEPAById(URI rdfId);
	
	SecDescription getSECById(String rdfId) throws URISyntaxException;
	
	SecDescription getSECById(URI rdfId);
	
	List<SepDescription> getAllSEPs();
	
	List<SepaDescription> getAllSEPAs();
	
	List<SepDescription> getSEPsByDomain(String domain);
	
	List<SepaDescription> getSEPAsByDomain(String domain);
	
	boolean deleteSEP(SepDescription sep);
	
	boolean deleteSEP(String rdfId);
	
	boolean deleteSEPA(SepaDescription sepa);
	
	boolean deleteSEPA(String rdfId);
	
	boolean exists(SepDescription sep);
	
	boolean exists(SepaDescription sepa);
	
	boolean update(SepDescription sep);
	
	boolean update(SepaDescription sepa);

	boolean exists(SecDescription sec);

	boolean update(SecDescription sec);

	boolean deleteSEC(SecDescription sec);
	
	boolean storeSEC(SecDescription sec);

	List<SecDescription> getAllSECs();
	
	StaticProperty getStaticPropertyById(String rdfId);
	
	EventStream getEventStreamById(String rdfId);
	
}
