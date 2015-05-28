package de.fzi.cep.sepa.storage.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rits.cloning.Cloner;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.api.StorageRequests;

public class InMemoryStorage implements StorageRequests {
	
	private Map<String, SEC> inMemorySECStorage;
	private Map<String, SEP> inMemorySEPStorage;
	private Map<String, SEPA> inMemorySEPAStorage;
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
		List<SEC> secs = sesameStorage.getAllSECs();
		secs.forEach(sec -> inMemorySECStorage.put(sec.getRdfId().toString(), sec));
	}
	
	private void initializeSEPAStorage() {
		inMemorySEPAStorage.clear();
		List<SEPA> sepas = sesameStorage.getAllSEPAs();
		sepas.forEach(sepa -> inMemorySEPAStorage.put(sepa.getRdfId().toString(), sepa));
	}
	
	private void initializeSEPStorage() {
		inMemorySEPStorage.clear();
		List<SEP> seps = sesameStorage.getAllSEPs();
		seps.forEach(sep -> inMemorySEPStorage.put(sep.getRdfId().toString(), sep));
		seps.forEach(sep -> sep.getEventStreams().forEach(eventStream -> inMemoryEventStreamStorage.put(eventStream.getRdfId().toString(), eventStream)));
	}

	@Override
	public boolean storeInvocableSEPAElement(InvocableSEPAElement element) {
		return sesameStorage.storeInvocableSEPAElement(element);
	}

	@Override
	public boolean storeSEP(SEP sep) {
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
	public boolean storeSEPA(SEPA sepa) {
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
	public SEP getSEPById(URI rdfId) {
		return cloner.deepClone(inMemorySEPStorage.get(rdfId.toString()));
	}

	@Override
	public SEP getSEPById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySEPStorage.get(rdfId));
	}

	@Override
	public SEPA getSEPAById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySEPAStorage.get(rdfId));
	}

	@Override
	public SEPA getSEPAById(URI rdfId) {
		return cloner.deepClone(inMemorySEPAStorage.get(rdfId.toString()));
	}

	@Override
	public SEC getSECById(String rdfId) throws URISyntaxException {
		return cloner.deepClone(inMemorySECStorage.get(rdfId));
	}

	@Override
	public SEC getSECById(URI rdfId) {
		return cloner.deepClone(inMemorySECStorage.get(rdfId.toString()));
	}

	@Override
	public List<SEP> getAllSEPs() {
		return new ArrayList<SEP>(inMemorySEPStorage.values());
	}

	@Override
	public List<SEPA> getAllSEPAs() {
		return new ArrayList<SEPA>(inMemorySEPAStorage.values());
	}

	@Override
	public List<SEP> getSEPsByDomain(String domain) {
		List<SEP> result = new ArrayList<>();
		for(SEP sep : getAllSEPs())
		{
			if (sep.getDomains().contains(domain)) result.add(cloner.deepClone(sep));
		}
		return result;
	}

	@Override
	public List<SEPA> getSEPAsByDomain(String domain) {
		List<SEPA> result = new ArrayList<>();
		for(SEPA sepa : getAllSEPAs())
		{
			if (sepa.getDomains().contains(domain)) result.add(cloner.deepClone(sepa));
		}
		return result;
	}

	@Override
	public boolean deleteSEP(SEP sep) {
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
	public boolean deleteSEPA(SEPA sepa) {
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
	public boolean exists(SEP sep) {
		return inMemorySEPStorage.containsKey(sep.getRdfId().toString());
	}

	@Override
	public boolean exists(SEPA sepa) {
		return inMemorySEPAStorage.containsKey(sepa.getRdfId().toString());
	}

	@Override
	public boolean update(SEP sep) {
		boolean success = sesameStorage.update(sep);
		initializeSEPStorage();
		return success;
	}

	@Override
	public boolean update(SEPA sepa) {
		boolean success = sesameStorage.update(sepa);
		initializeSEPAStorage();
		return success;
	}

	@Override
	public boolean exists(SEC sec) {
		return inMemorySECStorage.containsKey(sec.getRdfId().toString());
	}

	@Override
	public boolean update(SEC sec) {
		boolean success = sesameStorage.update(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean deleteSEC(SEC sec) {
		boolean success = sesameStorage.deleteSEC(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public boolean storeSEC(SEC sec) {
		boolean success = sesameStorage.storeSEC(sec);
		initializeSECStorage();
		return success;
	}

	@Override
	public List<SEC> getAllSECs() {
		return new ArrayList<SEC>(inMemorySECStorage.values());
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
