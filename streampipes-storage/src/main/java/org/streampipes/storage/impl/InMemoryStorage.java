package org.streampipes.storage.impl;

import com.rits.cloning.Cloner;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.storage.api.StorageRequests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStorage implements StorageRequests {
	
	private Map<String, DataSinkDescription> inMemorySECStorage;
	private Map<String, DataSourceDescription> inMemorySEPStorage;
	private Map<String, DataProcessorDescription> inMemorySEPAStorage;
	private Map<String, SpDataStream> inMemoryEventStreamStorage;
	private StorageRequests sesameStorage;
	private Cloner cloner;
	
	
	public InMemoryStorage(StorageRequests sesameStorage)
	{
		this.inMemorySECStorage = new HashMap<>();
		this.inMemorySEPAStorage = new HashMap<>();
		this.inMemorySEPStorage = new HashMap<>();
		this.inMemoryEventStreamStorage = new HashMap<>();
		this.sesameStorage = sesameStorage;
		this.cloner = new Cloner();
		init();
	}
	
	private void init()
	{
		initializeSECStorage();
		initializeSEPAStorage();
		initializeSEPStorage();
	}
	
	private void initializeSECStorage()	{
		inMemorySECStorage.clear();
		List<DataSinkDescription> secs = sesameStorage.getAllSECs();
		secs.forEach(sec -> inMemorySECStorage.put(sec.getElementId().toString(), sec));
	}
	
	private void initializeSEPAStorage() {
		inMemorySEPAStorage.clear();
		List<DataProcessorDescription> sepas = sesameStorage.getAllSEPAs();
		sepas.forEach(sepa -> inMemorySEPAStorage.put(sepa.getElementId().toString(), sepa));
	}
	
	private void initializeSEPStorage() {
		inMemorySEPStorage.clear();
		List<DataSourceDescription> seps = sesameStorage.getAllSEPs();
		seps.forEach(sep -> inMemorySEPStorage.put(sep.getElementId(), sep));
		seps.forEach(sep -> sep.getSpDataStreams().forEach(eventStream -> inMemoryEventStreamStorage.put(eventStream.getElementId(),
						eventStream)));
	}

	@Override
	public boolean storeInvocableSEPAElement(InvocableStreamPipesEntity element) {
		return sesameStorage.storeInvocableSEPAElement(element);
	}

	@Override
	public boolean storeSEP(DataSourceDescription sep) {
		boolean success = sesameStorage.storeSEP(sep);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean storeSEP(String jsonld) {
		boolean success = sesameStorage.storeSEP(jsonld);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean storeSEPA(DataProcessorDescription sepa) {
		boolean success = sesameStorage.storeSEPA(sepa);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public boolean storeSEPA(String jsonld) {
		boolean success = sesameStorage.storeSEP(jsonld);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public DataSourceDescription getSEPById(URI rdfId) {
		return cloner.deepClone(inMemorySEPStorage.get(rdfId.toString()));
	}

	@Override
	public DataSourceDescription getSEPById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySEPStorage.get(rdfId));
	}

	@Override
	public DataProcessorDescription getSEPAById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySEPAStorage.get(rdfId));
	}

	@Override
	public DataProcessorDescription getSEPAById(URI rdfId) {
		return cloner.deepClone(inMemorySEPAStorage.get(rdfId.toString()));
	}

	@Override
	public DataSinkDescription getSECById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySECStorage.get(rdfId));
	}

	@Override
	public DataSinkDescription getSECById(URI rdfId) {
		return cloner.deepClone(inMemorySECStorage.get(rdfId.toString()));
	}

	@Override
	public List<DataSourceDescription> getAllSEPs() {
		return new ArrayList<DataSourceDescription>(inMemorySEPStorage.values());
	}

	@Override
	public List<DataProcessorDescription> getAllSEPAs() {
		return new ArrayList<DataProcessorDescription>(inMemorySEPAStorage.values());
	}

	@Override
	public List<DataSourceDescription> getSEPsByDomain(String domain) {
		List<DataSourceDescription> result = new ArrayList<>();
		for(DataSourceDescription sep : getAllSEPs())
		{
			result.add(cloner.deepClone(sep));
		}
		return result;
	}

	@Override
	public List<DataProcessorDescription> getSEPAsByDomain(String domain) {
		List<DataProcessorDescription> result = new ArrayList<>();

		for(DataProcessorDescription sepa : getAllSEPAs())
		{
			result.add(cloner.deepClone(sepa));
		}
		return result;
	}

	@Override
	public boolean deleteSEP(DataSourceDescription sep) {
		boolean success = sesameStorage.deleteSEP(sep);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean deleteSEP(String rdfId) {
		boolean success = sesameStorage.deleteSEP(rdfId);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean deleteSEC(String rdfId) {
		boolean success = sesameStorage.deleteSEC(rdfId);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean deleteSEPA(DataProcessorDescription sepa) {
		boolean success = sesameStorage.deleteSEPA(sepa);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public boolean deleteSEPA(String rdfId) {
		boolean success = sesameStorage.deleteSEP(rdfId);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public boolean exists(DataSourceDescription sep) {
		return inMemorySEPStorage.containsKey(sep.getElementId());
	}

	@Override
	public boolean exists(DataProcessorDescription sepa) {
		return inMemorySEPAStorage.containsKey(sepa.getElementId());
	}

	@Override
	public boolean existsSepa(String rdfid) {
		return inMemoryEventStreamStorage.containsKey(rdfid);
	}

	@Override
	public boolean update(DataSourceDescription sep) {
		boolean success = sesameStorage.update(sep);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean update(DataProcessorDescription sepa) {
		boolean success = sesameStorage.update(sepa);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public boolean exists(DataSinkDescription sec) {
		return inMemorySECStorage.containsKey(sec.getElementId());
	}

	@Override
	public boolean update(DataSinkDescription sec) {
		boolean success = sesameStorage.update(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean deleteSEC(DataSinkDescription sec) {
		boolean success = sesameStorage.deleteSEC(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean storeSEC(DataSinkDescription sec) {
		boolean success = sesameStorage.storeSEC(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public List<DataSinkDescription> getAllSECs() {
		return new ArrayList<DataSinkDescription>(inMemorySECStorage.values());
	}

	@Override
	public StaticProperty getStaticPropertyById(String rdfId) {
		return sesameStorage.getStaticPropertyById(rdfId);
	}

	@Override
	public SpDataStream getEventStreamById(String rdfId) {
		return inMemoryEventStreamStorage.get(rdfId);
	}
}
