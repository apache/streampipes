/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto;

import com.github.jqudt.Multiplier;
import com.github.jqudt.Unit;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitFactory {
	
	private Model repos;

	private static UnitFactory factory = null;

	private UnitFactory() {
		repos = new LinkedHashModel();
		try {
			OntoReader.read(repos, "unit");
			OntoReader.read(repos, "qudt");
			OntoReader.read(repos, "quantity");
			OntoReader.read(repos, "ops.ttl");
		} catch (Exception exception) {
			throw new IllegalStateException(
				"Could not load the QUDT ontologies: " + exception.getMessage(), exception
			);
		}
	}

	public static UnitFactory getInstance() {
		if (factory == null) factory = new UnitFactory();
		return factory;
	}
	
	public Unit getUnit(String resource) {
		URI uri;
		try {
			uri = new URI(resource);
		} catch (URISyntaxException exception) {
			throw new IllegalStateException("Invalid URI: " + resource, exception);
		}
		return getUnit(uri);
	}

	public Unit getUnit(URI resource) {
		if (resource == null) throw new IllegalArgumentException("The URI cannot be null");

		ValueFactory f = ValueFactoryImpl.getInstance();
		org.eclipse.rdf4j.model.URI uri = f.createURI(resource.toString());
		
		Unit unit = new Unit();
		unit.setResource(resource);
		Multiplier multiplier = new Multiplier();

		try {
			Model statements = repos.filter(uri, null, null);
			if (statements.isEmpty())
				throw new IllegalStateException("No ontology entry found for: " + resource.toString());

			for (Statement statement : statements) {
				if (statement.getPredicate().equals(QUDT.SYMBOL)) {
					unit.setSymbol(statement.getObject().stringValue());
				} else if (statement.getPredicate().equals(QUDT.ABBREVIATION)) {
					unit.setAbbreviation(statement.getObject().stringValue());
				} else if (statement.getPredicate().equals(QUDT.CONVERSION_OFFSET)) {
					multiplier.setOffset(Double.parseDouble(statement.getObject().stringValue()));
				} else if (statement.getPredicate().equals(QUDT.CONVERSION_MULTIPLIER)) {
					multiplier.setMultiplier(Double.parseDouble(statement.getObject().stringValue()));
				} else if (statement.getPredicate().equals(RDFS.LABEL)) {
					unit.setLabel(statement.getObject().stringValue());
				} else if (statement.getPredicate().equals(RDF.TYPE)) {
					Object type = statement.getObject();
					if (type instanceof org.eclipse.rdf4j.model.URI) {
						org.eclipse.rdf4j.model.URI typeURI = (org.eclipse.rdf4j.model.URI)type;
						if (!shouldBeIgnored(typeURI)) {
							unit.setType(new URI(typeURI.stringValue()));
						}
					}
				}
			}
			unit.setMultiplier(multiplier);
		} catch (Exception exception) {
			throw new IllegalStateException("Could not create the unit: " + exception.getMessage(), exception);
		}

		return unit;
	}

	public List<String> getURIs(String type) {
		URI uri;
		try {
			uri = new URI(type);
		} catch (URISyntaxException exception) {
			throw new IllegalStateException("Invalid URI: " + type, exception);
		}
		return getURIs(uri);
	}
	
	public List<String> getURIs(URI type) {
		if (type == null) throw new IllegalArgumentException("The type cannot be null");

		ValueFactory f = ValueFactoryImpl.getInstance();
		org.eclipse.rdf4j.model.URI uri = f.createURI(type.toString());
		
		try {
			Model statements = repos.filter(null, null, uri);
			if (statements.isEmpty())
				return Collections.emptyList();

			List<String> units = new ArrayList<>();
			for (Statement statement : statements) {
				units.add(statement.getSubject().toString());
			}
			return units; 
		} catch (Exception exception) {
			throw new IllegalStateException("Error while getting the units: " + exception.getMessage(), exception);
		}
	}

	private boolean shouldBeIgnored(org.eclipse.rdf4j.model.URI typeURI) {
		// accept anything outside the QUDT namespace
		if (!typeURI.getNamespace().equals(QUDT.namespace)) return false;

		if (typeURI.equals(QUDT.SI_DERIVED_UNIT)) return true;
		if (typeURI.equals(QUDT.SI_BASE_UNIT)) return true;
		if (typeURI.equals(QUDT.SI_UNIT)) return true;
		if (typeURI.equals(QUDT.DERIVED_UNIT)) return true;
		if (typeURI.equals(QUDT.NOT_USED_WITH_SI_UNIT)) return true;
		if (typeURI.equals(QUDT.USED_WITH_SI_UNIT)) return true;

		// everything else is fine too
		return false;
	}
}
