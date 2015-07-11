package de.fzi.cep.sepa.storage.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rits.cloning.Cloner;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.api.StorageRequests;

public class InMemoryStorage implements StorageRequests {
	
	private Map<String, SecDescription> inMemorySECStorage;
	private Map<String, SepDescription> inMemorySEPStorage;
	private Map<String, SepaDescription> inMemorySEPAStorage;
	private Map<String, EventStream> inMemoryEventStreamStorage;
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
		List<SecDescription> secs = sesameStorage.getAllSECs();
		secs.forEach(sec -> inMemorySECStorage.put(sec.getRdfId().toString(), sec));
	}
	
	private void initializeSEPAStorage() {
		inMemorySEPAStorage.clear();
		List<SepaDescription> sepas = sesameStorage.getAllSEPAs();
		sepas.forEach(sepa -> inMemorySEPAStorage.put(sepa.getRdfId().toString(), sepa));
	}
	
	private void initializeSEPStorage() {
		inMemorySEPStorage.clear();
		List<SepDescription> seps = sesameStorage.getAllSEPs();
		seps.forEach(sep -> inMemorySEPStorage.put(sep.getRdfId().toString(), sep));
		seps.forEach(sep -> sep.getEventStreams().forEach(eventStream -> inMemoryEventStreamStorage.put(eventStream.getRdfId().toString(), eventStream)));
	}

	@Override
	public boolean storeInvocableSEPAElement(InvocableSEPAElement element) {
		return sesameStorage.storeInvocableSEPAElement(element);
	}

	@Override
	public boolean storeSEP(SepDescription sep) {
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
	public boolean storeSEPA(SepaDescription sepa) {
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
	public SepDescription getSEPById(URI rdfId) {
		return cloner.deepClone(inMemorySEPStorage.get(rdfId.toString()));
	}

	@Override
	public SepDescription getSEPById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySEPStorage.get(rdfId));
	}

	@Override
	public SepaDescription getSEPAById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySEPAStorage.get(rdfId));
	}

	@Override
	public SepaDescription getSEPAById(URI rdfId) {
		return cloner.deepClone(inMemorySEPAStorage.get(rdfId.toString()));
	}

	@Override
	public SecDescription getSECById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySECStorage.get(rdfId));
	}

	@Override
	public SecDescription getSECById(URI rdfId) {
		return cloner.deepClone(inMemorySECStorage.get(rdfId.toString()));
	}

	@Override
	public List<SepDescription> getAllSEPs() {
		return new ArrayList<SepDescription>(inMemorySEPStorage.values());
	}

	@Override
	public List<SepaDescription> getAllSEPAs() {
		return new ArrayList<SepaDescription>(inMemorySEPAStorage.values());
	}

	@Override
	public List<SepDescription> getSEPsByDomain(String domain) {
		List<SepDescription> result = new ArrayList<>();
		for(SepDescription sep : getAllSEPs())
		{
			if (sep.getDomains().contains(domain)) result.add(cloner.deepClone(sep));
		}
		return result;
	}

	@Override
	public List<SepaDescription> getSEPAsByDomain(String domain) {
		List<SepaDescription> result = new ArrayList<>();

		for(SepaDescription sepa : getAllSEPAs())
		{
			if (sepa.getDomains() != null) {
				if (sepa.getDomains().contains(domain)) result.add(cloner.deepClone(sepa));
			}
		}
		return result;
	}

	@Override
	public boolean deleteSEP(SepDescription sep) {
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
	public boolean deleteSEPA(SepaDescription sepa) {
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
	public boolean exists(SepDescription sep) {
		return inMemorySEPStorage.containsKey(sep.getRdfId().toString());
	}

	@Override
	public boolean exists(SepaDescription sepa) {
		return inMemorySEPAStorage.containsKey(sepa.getRdfId().toString());
	}

	@Override
	public boolean update(SepDescription sep) {
		boolean success = sesameStorage.update(sep);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean update(SepaDescription sepa) {
		boolean success = sesameStorage.update(sepa);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public boolean exists(SecDescription sec) {
		return inMemorySECStorage.containsKey(sec.getRdfId().toString());
	}

	@Override
	public boolean update(SecDescription sec) {
		boolean success = sesameStorage.update(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean deleteSEC(SecDescription sec) {
		boolean success = sesameStorage.deleteSEC(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean storeSEC(SecDescription sec) {
		boolean success = sesameStorage.storeSEC(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public List<SecDescription> getAllSECs() {
		return new ArrayList<SecDescription>(inMemorySECStorage.values());
	}

	@Override
	public StaticProperty getStaticPropertyById(String rdfId) {
		return sesameStorage.getStaticPropertyById(rdfId);
	}

	@Override
	public EventStream getEventStreamById(String rdfId) {
		return inMemoryEventStreamStorage.get(rdfId);
	}
}
