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
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.model.util.ModelUtils;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class Transformer {

	public static <T> Graph generateCompleteGraph(Graph graph, T element)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException {
		return new JsonLdTransformer().toJsonLd(element);
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

	public static <T> T fromJsonLd(Class<T> destination, String jsonld) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException {
		return new JsonLdTransformer().fromJsonLd(jsonld, destination);
	}

}
