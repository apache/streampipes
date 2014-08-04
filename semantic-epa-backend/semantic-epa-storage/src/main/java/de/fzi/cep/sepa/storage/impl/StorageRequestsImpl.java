package de.fzi.cep.sepa.storage.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManager;

import de.fzi.cep.sepa.model.impl.SEP;
import de.fzi.cep.sepa.model.impl.SEPA;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class StorageRequestsImpl implements StorageRequests {

	private StorageManager manager;
	private EntityManager entityManager;
	
	public StorageRequestsImpl()
	{
		manager = StorageManager.INSTANCE;
		entityManager = manager.getEntityManager();
	}
	
	//TODO: exception handling
	
	@Override
	public boolean storeSEP(SEP sep) {
		entityManager.persist(sep);
		return true;
	}

	@Override
	public boolean storeSEP(String jsonld) {
		//todo
		return false;
	}

	@Override
	public boolean storeSEPA(SEPA sepa) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean storeSEPA(String jsonld) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SEP getSEPById(URI rdfId) {
		return entityManager.find(SEP.class, rdfId);
	}

	@Override
	public SEP getSEPById(String rdfId) throws URISyntaxException {
		return getSEPById(new URI(rdfId));
	}

	@Override
	public List<SEP> getAllSEPs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SEPA> getAllSEPAs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteSEP(SEP sep) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteSEP(String rdfId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteSEPA(SEPA sepa) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteSEPA(String rdfId) {
		// TODO Auto-generated method stub
		return false;
	}

}
