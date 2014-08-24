package de.fzi.cep.sepa.storage.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.crypto.SealedObject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.clarkparsia.empire.impl.RdfQuery;
import com.clarkparsia.empire.util.EmpireUtil;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;
import de.fzi.cep.sepa.storage.util.Transformer;

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
		SEP sep = Transformer.fromJsonLd(SEP.class, jsonld);
		return storeSEP(sep);
	}

	@Override
	public boolean storeSEPA(SEPA sepa) {
		entityManager.persist(sepa);
		return true;
	}

	@Override
	public boolean storeSEPA(String jsonld) {
		SEPA sepa = Transformer.fromJsonLd(SEPA.class, jsonld);
		return storeSEPA(sepa);
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
		Query query = entityManager.createQuery(QueryBuilder.buildListSEPQuery());
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, SEP.class);
		System.out.println(query.toString());
		return query.getResultList();
	}

	@Override
	public List<SEPA> getAllSEPAs() {
		Query query = entityManager.createQuery(QueryBuilder.buildListSEPAQuery());
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, SEPA.class);
		System.out.println(query.toString());
		return query.getResultList();
	}

	@Override
	public boolean deleteSEP(SEP sep) {
		entityManager.remove(sep);
		return true;
	}

	@Override
	public boolean deleteSEP(String rdfId) {
		SEP sep = entityManager.find(SEP.class, rdfId);
		entityManager.remove(sep);
		return true;
	}

	@Override
	public boolean deleteSEPA(SEPA sepa) {
		entityManager.remove(sepa);
		return true;
	}

	@Override
	public boolean deleteSEPA(String rdfId) {
		SEPA sepa = entityManager.find(SEPA.class, rdfId);
		return deleteSEPA(sepa);
	}

	@Override
	public boolean exists(SEP sep) {
		SEP storedSEP = entityManager.find(SEP.class, sep.getRdfId());
		return storedSEP != null ? true : false;
	}

	@Override
	public boolean exists(SEPA sepa) {
		SEPA storedSEPA = entityManager.find(SEPA.class, sepa.getRdfId());
		return storedSEPA != null ? true : false;
	}

	@Override
	public boolean update(SEP sep) {
		return deleteSEP(sep) && storeSEP(sep);
	}

	@Override
	public boolean update(SEPA sepa) {
		return deleteSEPA(sepa) && storeSEPA(sepa);
	}

	@Override
	public List<SEP> getSEPsByDomain(String domain) {
		Query query = entityManager.createQuery(QueryBuilder.buildSEPByDomainQuery(domain));
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, SEP.class);
		System.out.println(query.toString());
		return query.getResultList();
	}

	@Override
	public List<SEPA> getSEPAsByDomain(String domain) {
		Query query = entityManager.createQuery(QueryBuilder.buildSEPAByDomainQuery(domain));
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, SEPA.class);
		System.out.println(query.toString());
		return query.getResultList();
	}

}
