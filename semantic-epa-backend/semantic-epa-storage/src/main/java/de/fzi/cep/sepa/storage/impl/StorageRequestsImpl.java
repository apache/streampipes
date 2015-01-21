package de.fzi.cep.sepa.storage.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.crypto.SealedObject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.clarkparsia.empire.impl.RdfQuery;
import com.clarkparsia.empire.util.EmpireUtil;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
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

	@Override
	public SEPA getSEPAById(String rdfId) throws URISyntaxException {
		return getSEPAById(new URI(rdfId));
	}

	@Override
	public SEPA getSEPAById(URI rdfId) {
		return entityManager.find(SEPA.class, rdfId);
	}

	@Override
	public SEC getSECById(String rdfId) throws URISyntaxException {
		return getSECById(new URI(rdfId));
	}

	@Override
	public SEC getSECById(URI rdfId) {
		return entityManager.find(SEC.class, rdfId);
	}

	@Override
	public boolean exists(SEC sec) {
		SEC storedSEC = entityManager.find(SEC.class, sec.getRdfId());
		return storedSEC != null ? true : false;
	}

	@Override
	public boolean update(SEC sec) {
		return deleteSEC(sec) && storeSEC(sec);
		
	}

	@Override
	public boolean deleteSEC(SEC sec) {
		entityManager.remove(sec);
		return true;
	}

	@Override
	public boolean storeSEC(SEC sec) {
		entityManager.persist(sec);
		return true;
	}

	@Override
	public List<SEC> getAllSECs() {
		Query query = entityManager.createQuery(QueryBuilder.buildListSECQuery());
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, SEC.class);
		return query.getResultList();
	}

	@Override
	public StaticProperty getStaticPropertyById(String rdfId) {
		return entityManager.find(StaticProperty.class, URI.create(rdfId));
	}

	@Override
	public boolean storeInvocableSEPAElement(InvocableSEPAElement element) {
		entityManager.persist(element);
		return true;
	}

	

}
