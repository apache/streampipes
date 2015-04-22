package de.fzi.cep.sepa.storage.util;

import java.beans.Introspector;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.clarkparsia.empire.annotation.RdfGenerator;
import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.Configuration;
import de.fzi.cep.sepa.model.AbstractSEPAElement;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class Transformer {


	public static <T> Graph generateCompleteGraph(Graph graph, T element)
			throws IllegalArgumentException, IllegalAccessException,
			SecurityException {
		if (element != null) {
			try {
				Graph temp = RdfGenerator.asRdf(element);
				graph = appendGraph(graph, temp);

				final String packageName = getPackageName(element.getClass().getSimpleName().replace("Impl", ""));
				final String className = element.getClass().getSimpleName()
						.replace("Impl", "");

				Method[] ms;
				ms = Class.forName(packageName + className).getMethods();

				for (Method m : ms) {
					if (m.getName().startsWith("get")) {
						if (Collection.class
								.isAssignableFrom(m.getReturnType())) {
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
											generateCompleteGraph(graph, e);
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
								generateCompleteGraph(graph,
										(AbstractSEPAElement) m.invoke(element));
							}
						}
					}
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidRdfException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println(element.getClass().getCanonicalName());
			}

		}

		return graph;

	}
	
	public static String getPackageName(String className)
	{
		String[] abstractClasses = {"AbstractSEPAElement", "NamedSEPAElement", "UnnamedSEPAElement", "InvocableSEPAElement"};
		String[] graphClasses = {"SEC", "SEP", "SEPA", "SEPAInvocationGraph", "SECInvocationGraph"};
		String[] modelClasses = {"MatchingStaticProperty",
				"Domain", 
				"EventGrounding", 
				"EventProperty", 
				"EventQuality", 
				"EventSchema", 
				"EventSource", 
				"EventStream", 
				"MeasurementUnit", 
				"Namespace", 
				"Pipeline", 
				"PipelineElement", 
				"SEPAFactory", 
				"StaticProperty", 
				"TransportFormat", 
				"TransportProtocol", 
				"OneOfStaticProperty", 
				"FreeTextStaticProperty", 
				"AnyStaticProperty", 
				"Option", 
				"MappingProperty", 
				"EventPropertyPrimitive", 
				"EventPropertyNested", 
				"EventPropertyList", 
				"MappingPropertyUnary",
				"MappingPropertyNary"};
		String[] outputClasses = {"ListOutputStrategy", "AppendOutputStrategy", "OutputStrategy", "OutputStrategyParameter", "OutputStrategyType", "RenameOutputStrategy", "CustomOutputStrategy", "FixedOutputStrategy"};
		
		if (contains(className, abstractClasses)) 
			{
				return "de.fzi.cep.sepa.model.";
			}
		else if (contains(className, graphClasses)) 
			{
				return "de.fzi.cep.sepa.model.impl.graph.";
			}
		else if (contains(className, outputClasses)) 
			{
				return "de.fzi.cep.sepa.model.impl.output.";
			}
		else if (contains(className, modelClasses)) 
			{
				return "de.fzi.cep.sepa.model.impl.";
			}
		else 
			{
				System.out.println("MISSING: " +className);
				throw new IllegalArgumentException();
			}
	}
	
	private static boolean contains(String value, String[] list)
	{
		return Arrays.asList(list).contains(value);
	}

	public static Field[] getAllFields(
			Class<? extends AbstractSEPAElement> clazz)
			throws SecurityException {
		List<Field> fields = new ArrayList<Field>();
		for (Method m : clazz.getMethods()) {

			if (m.getName().startsWith("get")) {
				try {
					fields.add(clazz.getField(Introspector.decapitalize(m
							.getName().replaceFirst("get", ""))));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return fields.toArray(new Field[0]);
	}

	public static Object getList(Object obj, String name)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		for (Method m : obj.getClass().getMethods()) {
			if (m.getName().startsWith("get")
					&& m.getName().toLowerCase().endsWith(name)) {
				return m.invoke(obj);
			}
		}
		return null;
	}

	public static Graph appendGraph(Graph originalGraph, Graph appendix) {
		Iterator<Statement> it = appendix.iterator();

		while (it.hasNext()) {
			originalGraph.add(it.next());
		}
		return originalGraph;
	}

	public static <T> T fromJsonLd(Class<T> destination, String jsonld) {
		
		EntityManager manager = StorageManager.INSTANCE.getTempEntityManager();
		String uri = null;
		InputStream stream = new ByteArrayInputStream(
				jsonld.getBytes(StandardCharsets.UTF_8));
		Model statements;
		try {
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
				StorageManager.INSTANCE.getTempConnection().add(s);
			}
		} catch (RDFParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedRDFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		T result = manager.find(destination, java.net.URI.create(uri));
		StorageUtils.emptyRepository(StorageManager.INSTANCE.getTempConnection());
		return result;
	}

}
