/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.rdf4j.impl;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.empire.core.empire.impl.RdfQuery;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.storage.Rdf4JStorageManager;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.rdf4j.sparql.QueryBuilder;
import org.streampipes.storage.rdf4j.util.Transformer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class SesameStorageRequests implements IPipelineElementDescriptionStorage {

	private Rdf4JStorageManager manager;
	private EntityManager entityManager;
	
	public SesameStorageRequests()
	{
		manager = Rdf4JStorageManager.INSTANCE;
		entityManager = manager.getEntityManager();
	}
	
	//TODO: exception handling
	
	@Override
	public boolean storeSEP(DataSourceDescription sep) {
		if (exists(sep)) return false;
		entityManager.persist(sep);
		return true;
	}

	@Override
	public boolean storeSEP(String jsonld) {
		DataSourceDescription sep;
		try {
			sep = Transformer.fromJsonLd(DataSourceDescription.class, jsonld);
			return storeSEP(sep);
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedRDFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean storeSEPA(DataProcessorDescription sepa) {
		if (existsSepa(sepa.getElementId())) return false;
		entityManager.persist(sepa);
		return true;
	}

	@Override
	public boolean existsSepa(String rdfId) {
		DataProcessorDescription storedSEPA = entityManager.find(DataProcessorDescription.class, rdfId);
		return storedSEPA != null;
	}

	@Override
	public boolean storeSEPA(String jsonld) {
		DataProcessorDescription sepa;
		try {
			sepa = Transformer.fromJsonLd(DataProcessorDescription.class, jsonld);
			return storeSEPA(sepa);
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedRDFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public DataSourceDescription getSEPById(URI rdfId) {
		return entityManager.find(DataSourceDescription.class, rdfId);
	}

	@Override
	public DataSourceDescription getSEPById(String rdfId) throws URISyntaxException {
		return getSEPById(new URI(rdfId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataSourceDescription> getAllSEPs() {
		Query query = entityManager.createQuery(QueryBuilder.buildListSEPQuery());
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataSourceDescription.class);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataProcessorDescription> getAllSEPAs() {
		Query query = entityManager.createQuery(QueryBuilder.buildListSEPAQuery());
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataProcessorDescription.class);
		return query.getResultList();
	}

	@Override
	public boolean deleteSEP(DataSourceDescription sep) {
		deleteSEP(sep.getElementId());
		return true;
	}

	@Override
	public boolean deleteSEP(String rdfId) {
		DataSourceDescription sep = entityManager.find(DataSourceDescription.class, rdfId);
		entityManager.remove(sep);
		return true;
	}

	@Override
	public boolean deleteSEPA(DataProcessorDescription sepa) {
		deleteSEPA(sepa.getElementId());
		return true;
	}

	@Override
	public boolean deleteSEPA(String rdfId) {
		DataProcessorDescription sepa = entityManager.find(DataProcessorDescription.class, rdfId);
		entityManager.remove(sepa);
		return true;
	}

	@Override
	public boolean exists(DataSourceDescription sep) {
		DataSourceDescription storedSEP = entityManager.find(DataSourceDescription.class, sep.getElementId());
		return storedSEP != null ? true : false;
	}

	@Override
	public boolean exists(DataProcessorDescription sepa) {
		DataProcessorDescription storedSEPA = entityManager.find(DataProcessorDescription.class, sepa.getElementId());
		return storedSEPA != null ? true : false;
	}

	@Override
	public boolean update(DataSourceDescription sep) {
		return deleteSEP(sep) && storeSEP(sep);
	}

	@Override
	public boolean update(DataProcessorDescription sepa) {
		return deleteSEPA(sepa) && storeSEPA(sepa);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataSourceDescription> getSEPsByDomain(String domain) {
		Query query = entityManager.createQuery(QueryBuilder.buildSEPByDomainQuery(domain));
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataSourceDescription.class);
		System.out.println(query.toString());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataProcessorDescription> getSEPAsByDomain(String domain) {
		Query query = entityManager.createQuery(QueryBuilder.buildSEPAByDomainQuery(domain));
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataProcessorDescription.class);
		System.out.println(query.toString());
		return query.getResultList();
	}

	@Override
	public DataProcessorDescription getSEPAById(String rdfId) throws URISyntaxException {
		return getSEPAById(new URI(rdfId));
	}

	@Override
	public DataProcessorDescription getSEPAById(URI rdfId) {
		return entityManager.find(DataProcessorDescription.class, rdfId);
	}

	@Override
	public DataSinkDescription getSECById(String rdfId) throws URISyntaxException {
		return getSECById(new URI(rdfId));
	}

	@Override
	public DataSinkDescription getSECById(URI rdfId) {
		return entityManager.find(DataSinkDescription.class, rdfId);
	}

	@Override
	public boolean exists(DataSinkDescription sec) {
		DataSinkDescription storedSEC = entityManager.find(DataSinkDescription.class, sec.getElementId());
		return storedSEC != null ? true : false;
	}

	@Override
	public boolean update(DataSinkDescription sec) {
		return deleteSEC(sec) && storeSEC(sec);
		
	}

	@Override
	public boolean deleteSEC(DataSinkDescription sec) {
		return deleteSEC(sec.getElementId());
	}

	@Override
	public boolean deleteSEC(String rdfId) {
		DataSinkDescription sec = entityManager.find(DataSinkDescription.class, rdfId);
		entityManager.remove(sec);
		return true;
	}

	@Override
	public boolean storeSEC(DataSinkDescription sec) {
		if (exists(sec)) return false;
		entityManager.persist(sec);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataSinkDescription> getAllSECs() {
		Query query = entityManager.createQuery(QueryBuilder.buildListSECQuery());
		query.setHint(RdfQuery.HINT_ENTITY_CLASS, DataSinkDescription.class);
		return query.getResultList();
	}

	@Override
	public StaticProperty getStaticPropertyById(String rdfId) {
		return entityManager.find(StaticProperty.class, URI.create(rdfId));
	}

	@Override
	public boolean storeInvocableSEPAElement(InvocableStreamPipesEntity element) {
		entityManager.persist(element);
		return true;
	}

	@Override
	public SpDataStream getEventStreamById(String rdfId) {
		return entityManager.find(SpDataStream.class, URI.create(rdfId));
	}

	

}
