package de.fzi.cep.sepa.model.transform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.clarkparsia.empire.annotation.RdfGenerator;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.model.AbstractSEPAElement;
import de.fzi.cep.sepa.model.util.ModelUtils;

public class JsonLdTransformer implements RdfTransformer {

	private final static Logger logger = Logger.getLogger(JsonLdTransformer.class.getName());
	
	@Override
	public <T> Graph toJsonLd(T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException {
		return toJsonLd(new GraphImpl(), element);
	}
	
	private <T> Graph toJsonLd(Graph graph, T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException
	{
		if (element != null) {
			Graph temp = RdfGenerator.asRdf(element);
			graph = appendGraph(graph, temp);

			Method[] ms;
			String className = element.getClass().getName();
			String fixedClassName = fixClassName(className);
			ms = Class.forName(fixedClassName).getMethods();
			System.out.println(element.getClass().getName());
			for (Method m : ms) {
				if (m.getName().startsWith("get")) {
					if (Collection.class
							.isAssignableFrom(m.getReturnType())) {
						System.out.println(m.getName() +", " +m.getGenericReturnType().getTypeName());
						String genericClassName = ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0].getTypeName();
						if(!(genericClassName.startsWith("java.lang.")))
						{
							if(!(genericClassName.startsWith("java.net")))
							{
								@SuppressWarnings("unchecked")
								List<? extends AbstractSEPAElement> listElements = (List<? extends AbstractSEPAElement>) m
										.invoke(element);
								if (listElements != null) {
									for (AbstractSEPAElement e : listElements) {
										toJsonLd(graph, e);
									}
								}
							}
						}
					} else {
						if (!m.getReturnType().isPrimitive()
								&& !m.getReturnType().getCanonicalName()
										.startsWith("java.lang.")
								&& !m.getReturnType().getCanonicalName().startsWith("java.net")
								&& !m.getReturnType()
										.getCanonicalName()
										.startsWith(
												"com.clarkparsia.empire.")
												
								&& !m.getName().equals("getUntypedRuntimeFormat")
								&& !m.getName().equals("getRuntimeFormat")
								&& !m.getName().equals("getFullPropertyName")
								) {
							toJsonLd(graph,
									(AbstractSEPAElement) m.invoke(element));
						}
					}
				}
			}
		}
		return graph;
	}
	
	private String fixClassName(String className) {
		int index = className.lastIndexOf("impl.");
		return new StringBuilder(className).replace(index, index+5,"").toString().replace("Impl", "");
	}

	private Graph appendGraph(Graph originalGraph, Graph appendix) {
		Iterator<Statement> it = appendix.iterator();

		while (it.hasNext()) {
			originalGraph.add(it.next());
		}
		return originalGraph;
	}

	@Override
	public <T> T fromJsonLd(String json, Class<T> destination) throws RDFParseException, UnsupportedRDFormatException, IOException, RepositoryException {
		
		EntityManager manager = EmpireManager.INSTANCE.getTempEntityManager();
		String uri = null;
		InputStream stream = new ByteArrayInputStream(
				json.getBytes(StandardCharsets.UTF_8));
		Model statements;
		statements = Rio.parse(stream, "", Configuration.RDF_FORMAT);

		Iterator<Statement> st = statements.iterator();

		while (st.hasNext()) {
			Statement s = st.next();
			if ((s.getPredicate().equals(RDF.TYPE))) {
				
				if (s.getObject()
						.stringValue()
						.equals(de.fzi.cep.sepa.model.vocabulary.SEPA.SEMANTICEVENTPROCESSINGAGENT
								.toSesameURI().toString())) {
					uri = s.getSubject().toString();
				} else if (s
						.getObject()
						.stringValue()
						.equals(de.fzi.cep.sepa.model.vocabulary.SEPA.SEMANTICEVENTPRODUCER
								.toSesameURI().toString())) {
					uri = s.getSubject().toString();
				} else if (s
						.getObject()
						.stringValue()
						.equals(de.fzi.cep.sepa.model.vocabulary.SEPA.SEMANTICEVENTCONSUMER
								.toSesameURI().toString())) {
					uri = s.getSubject().toString(); 
				} else if (s
						.getObject()
						.stringValue()
						.equals(de.fzi.cep.sepa.model.vocabulary.SEPA.SEPAINVOCATIONGRAPH
								.toSesameURI().toString())) {
					uri = s.getSubject().toString(); 
				}  else if (s
						.getObject()
						.stringValue()
						.equals(de.fzi.cep.sepa.model.vocabulary.SEPA.SECINVOCATIONGRAPH
								.toSesameURI().toString())) {
					uri = s.getSubject().toString(); 
				}
				
			}
			EmpireManager.INSTANCE.getTempConnection().add(s);
			}
		T result = manager.find(destination, java.net.URI.create(uri));
		EmpireManager.INSTANCE.clearRepository();
		return result;
	}
}
