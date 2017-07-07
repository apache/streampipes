package org.streampipes.manager.verification.extractor;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.manager.verification.ElementVerifier;
import org.streampipes.manager.verification.SecVerifier;
import org.streampipes.manager.verification.SepVerifier;
import org.streampipes.manager.verification.SepaVerifier;
import org.streampipes.model.vocabulary.SEPA;

public class TypeExtractor {

	private static final Logger logger = Logger.getAnonymousLogger();

	private String graphData;
	
	public TypeExtractor(String graphData)
	{
		this.graphData = graphData;
	}
	
	public ElementVerifier<?> getTypeVerifier() throws SepaParseException
	{
		List<Statement> typeDefinitions = getModel().stream().filter(stmt -> stmt.getPredicate().equals(RDF.TYPE)).collect(Collectors.toList());
		typeDefinitions.forEach(typeDef -> typeDef.getObject());
		return getTypeDef(typeDefinitions.stream().filter(stmt -> 
			((stmt.getObject().toString().equals(ec())) || 
			(stmt.getObject().toString().equals(epa())) ||
			(stmt.getObject().toString().equals(ep())))).findFirst());
	}

	private ElementVerifier<?> getTypeDef(Optional<Statement> typeStatement) throws SepaParseException {
		if (!typeStatement.isPresent()) throw new SepaParseException();
		else 
		{
			Statement stmt = typeStatement.get();
			if (stmt.getObject().toString().equals(ep())) { logger.info("Detected type sep"); return new SepVerifier(graphData); }
			else if (stmt.getObject().toString().equals(epa())) { logger.info("Detected type sepa"); return new SepaVerifier(graphData); }
			else if (stmt.getObject().toString().equals(ec())) { logger.info("Detected type sec"); return new SecVerifier(graphData); }
			else throw new SepaParseException();
		}
	}
	
	private Model getModel() throws SepaParseException
	{
		return StatementBuilder.extractStatements(graphData);
	}
	
	private static final String ep()
	{
		return SEPA.SEMANTICEVENTPRODUCER.toSesameURI().toString();
	}
	
	private static final String epa()
	{
		return SEPA.SEMANTICEVENTPROCESSINGAGENT.toSesameURI().toString();
	}
	
	private static final String ec()
	{
		return SEPA.SEMANTICEVENTCONSUMER.toSesameURI().toString();
	}
	
}
