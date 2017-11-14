package org.streampipes.storage.api;

import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.staticproperty.StaticProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface StorageRequests {
	
	boolean storeInvocableSEPAElement(InvocableStreamPipesEntity element);

	boolean storeSEP(DataSourceDescription sep);
	
	boolean storeSEP(String jsonld);
	
	boolean storeSEPA(DataProcessorDescription sepa);
	
	boolean storeSEPA(String jsonld);
	
	DataSourceDescription getSEPById(URI rdfId);
	
	DataSourceDescription getSEPById(String rdfId) throws URISyntaxException;
	
	DataProcessorDescription getSEPAById(String rdfId) throws URISyntaxException;
	
	DataProcessorDescription getSEPAById(URI rdfId);
	
	DataSinkDescription getSECById(String rdfId) throws URISyntaxException;
	
	DataSinkDescription getSECById(URI rdfId);
	
	List<DataSourceDescription> getAllSEPs();
	
	List<DataProcessorDescription> getAllSEPAs();
	
	List<DataSourceDescription> getSEPsByDomain(String domain);
	
	List<DataProcessorDescription> getSEPAsByDomain(String domain);
	
	boolean deleteSEP(DataSourceDescription sep);
	
	boolean deleteSEP(String rdfId);
	
	boolean deleteSEPA(DataProcessorDescription sepa);
	
	boolean deleteSEPA(String rdfId);
	
	boolean exists(DataSourceDescription sep);
	
	boolean exists(DataProcessorDescription sepa);

	boolean existsSepa(String rdfid);
	
	boolean update(DataSourceDescription sep);
	
	boolean update(DataProcessorDescription sepa);

	boolean exists(DataSinkDescription sec);

	boolean update(DataSinkDescription sec);

	boolean deleteSEC(DataSinkDescription sec);
	
	boolean storeSEC(DataSinkDescription sec);

	List<DataSinkDescription> getAllSECs();
	
	StaticProperty getStaticPropertyById(String rdfId);
	
	SpDataStream getEventStreamById(String rdfId);
	
}
