package de.fzi.cep.sepa.storage.util;

import java.beans.Introspector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.clarkparsia.empire.annotation.RdfGenerator;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.AbstractSEPAElement;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SourceClient;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.SEPAManager;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class Transformer {

	private StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	
	public static <T> Graph generateCompleteGraph(Graph graph, T element)
			throws IllegalArgumentException, IllegalAccessException,
			SecurityException {
		if (element != null) {
			try {
				Graph temp = RdfGenerator.asRdf(element);
				graph = appendGraph(graph, temp);

				final String packageName = getPackageName(element.getClass().getSimpleName());
				final String className = element.getClass().getSimpleName()
						.replace("Impl", "");

				Method[] ms;

				ms = Class.forName(packageName + className).getMethods();

				for (Method m : ms) {
					if (m.getName().startsWith("get")) {
						if (Collection.class
								.isAssignableFrom(m.getReturnType())) {
							String genericClassName = ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0].getTypeName();
							System.out.println(genericClassName);
							if(!(genericClassName.startsWith("java.lang.")))
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
						} else {
							if (!m.getReturnType().isPrimitive()
									&& !m.getReturnType().getCanonicalName()
											.startsWith("java.lang.")
									&& !m.getReturnType()
											.getCanonicalName()
											.startsWith(
													"com.clarkparsia.empire.")) {
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
			}

		}

		return graph;

	}
	
	public static String getPackageName(String className)
	{
		String[] abstractClasses = {"AbstractSEPAElement", "NamedSEPAElement", "UnnamedSEPAElement"};
		String[] graphClasses = {"SEC", "SEP", "SEPA", "SEPAInvocationGraph"};
		String[] modelClasses = {"Domain", "EventGrounding", "EventProperty", "EventQuality", "EventSchema", "EventSource", "EventStream", "MeasurementUnit", "Namespace", "Pipeline", "PipelineElement", "SEPAFactory", "StaticProperty", "TransportFormat", "TransportProtocol"};
		String[] outputClasses = {"AppendOutputStrategy", "OutputStrategy", "OutputStrategyParameter", "OutputStrategyType", "RenameOutputStrategy"};
		
		if (contains(className, abstractClasses)) return "de.fzi.cep.sepa.model.";
		else if (contains(className, graphClasses)) return "de.fzi.cep.sepa.model.impl.graph.";
		else if (contains(className, outputClasses)) return "de.fzi.cep.sepa.model.impl.output.";
		else return "de.fzi.cep.sepa.model.impl.";
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
			statements = Rio.parse(stream, "", RDFFormat.JSONLD);

			Iterator<Statement> st = statements.iterator();

			Class<? extends AbstractSEPAElement> toParse;
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
	
	public static SEPAClient toSEPAClientModel(SEPA sepa)
	{
		SEPAClient client = new SEPAClient(sepa.getName(), sepa.getDescription(), sepa.getDomains());
		client.setInputNodes(sepa.getEventStreams().size());
		client.setElementId(sepa.getRdfId().toString());
		client.setIconUrl(sepa.getIconUrl());
		client.setInputNodes(sepa.getEventStreams().size());
		
		
		return client;
	}
	
	public static List<SEPAClient> toSEPAClientModel(List<SEPA> sepas)
	{
		List<SEPAClient> result = new ArrayList<SEPAClient>();
		for(SEPA sepa : sepas) result.add(toSEPAClientModel(sepa));
		return result;
	}
	
	public static SEPA fromSEPAClientModel(SEPAClient sepaClient)
	{
		//return StorageManager.INSTANCE.getStorageAPI().getSEPAById(sepaClient.getElementId());
		return null;
	}
	
	public static SourceClient toSEPClientModel(SEP sep)
	{
		return null;
	}
	
	public static StreamClient toStreamClientModel(SEP sep, EventStream stream)
	{
		StreamClient client = new StreamClient(stream.getName(), stream.getDescription(), sep.getRdfId().toString());
		client.setIconUrl(stream.getIconUrl());
		client.setElementId(stream.getRdfId().toString());
		return client;
	}
	
	public static EventStream fromStreamClientModel(StreamClient client)
	{
		return StorageManager.INSTANCE.getEntityManager().find(EventStream.class, client.getElementId());
	}
	
	public static SourceClient toSourceClientModel(SEP sep)
	{
		SourceClient client = new SourceClient(sep.getName(), sep.getDescription(), sep.getDomains());
		client.setIconUrl(sep.getIconUrl());
		client.setElementId(sep.getRdfId().toString());
		return client;
	}
	
	public static List<SourceClient> toSourceClientModel(List<SEP> seps)
	{
		List<SourceClient> result = new ArrayList<SourceClient>();
		for(SEP sep : seps)
			result.add(toSourceClientModel(sep));
		return result;
			
	}
	
	public static SEP fromSourceClientModel(SourceClient client)
	{
		return StorageManager.INSTANCE.getEntityManager().find(SEP.class, client.getElementId());
	}
	
	
	public static List<StreamClient> toStreamClientModel(List<SEP> seps)
	{
		List<StreamClient> result = new ArrayList<StreamClient>();
		for(SEP sep : seps)
		{
			result.addAll(toStreamClientModel(sep));
		}
		return result;
	}
	
	public static List<StreamClient> toStreamClientModel(SEP sep)
	{
		List<StreamClient> result = new ArrayList<StreamClient>();
		for(EventStream stream : sep.getEventStreams())
			result.add(toStreamClientModel(sep, stream));
		return result;
	}

}
