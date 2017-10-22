package org.streampipes.model.transform;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.GraphImpl;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.empire.core.empire.annotation.RdfGenerator;
import org.streampipes.model.AbstractSEPAElement;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.vocabulary.SEPA;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

public class JsonLdTransformer implements RdfTransformer {

    private final static Logger logger = Logger.getLogger(JsonLdTransformer.class.getName());

    @Override
    public <T> Graph toJsonLd(T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException {
        return toJsonLd(new GraphImpl(), element);
    }

    private <T> Graph toJsonLd(Graph graph, T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException {
        if (element != null) {
            Graph temp = RdfGenerator.asRdf(element);
            graph = appendGraph(graph, temp);

            Method[] ms;
            String className = element.getClass().getName();
            String fixedClassName = fixClassName(className, element.getClass().getSimpleName());
            ms = Class.forName(fixedClassName).getMethods();
            for (Method m : ms) {
                if (m.getName().startsWith("get")) {
                    if (Collection.class
                            .isAssignableFrom(m.getReturnType())) {
                        String genericClassName = ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0].getTypeName();
                        if (!(genericClassName.startsWith("java.lang."))) {
                            if (!(genericClassName.startsWith("java.net"))) {
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
                                        "org.streampipes.empire.")

                                && !m.getName().equals("getUntypedRuntimeFormat")
                                && !m.getName().equals("getRuntimeFormat")
                                && !m.getName().equals("getFullPropertyName")
                                && !m.getName().equals("getStaticPropertyType")
                                && !m.getName().equals("getStreamRequirements")
                                && isRdfPropertyAnnotated(m, Class.forName(fixedClassName))
                                ) {
                            toJsonLd(graph,
                                    m.invoke(element));
                        }
                    }
                }
            }
        }
        return graph;
    }

    private boolean isRdfPropertyAnnotated(Method m, Class ms) {

        try {
            Field field = ms.getDeclaredField(toField(m));
            field.setAccessible(true);
            if (field.getAnnotation(RdfProperty.class) != null) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchFieldException e) {
            //System.out.println(ms.getCanonicalName());
            //System.out.println(m.getName());
            //System.out.println(e.getClass().getCanonicalName());
            return true;
        }
    }

    public static void main(String[] args) throws NoSuchMethodException {
        boolean result = new JsonLdTransformer()
                .isRdfPropertyAnnotated(StaticProperty
                        .class
                        .getDeclaredMethod("getStaticPropertyType"), StaticProperty.class);
        System.out.println(result);
    }

    private String toField(Method m) {
        String propertyName = m.getName().replace("get", "");
        return Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
    }


    private String fixClassName(String className, String simpleName) {
        int index = className.lastIndexOf("impl." + simpleName);
        if (index != -1 && count(className) > 1) {
            return new StringBuilder(className).replace(index, index + 5, "").toString().replace("Impl", "");
        } else {
            return className;
        }
    }

    private int count(String className) {
        String substr = "impl.";
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = className.indexOf(substr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += substr.length();
            }
        }
        return count;
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
        statements = Rio.parse(stream, "", RDFFormat.JSONLD);

        Iterator<Statement> st = statements.iterator();

        while (st.hasNext()) {
            Statement s = st.next();
            if ((s.getPredicate().equals(RDF.TYPE))) {

                if (s.getObject()
                        .stringValue()
                        .equals(SEPA.SEMANTICEVENTPROCESSINGAGENT
                                .toSesameURI().toString())) {
                    uri = s.getSubject().toString();
                } else if (s
                        .getObject()
                        .stringValue()
                        .equals(SEPA.SEMANTICEVENTPRODUCER
                                .toSesameURI().toString())) {
                    uri = s.getSubject().toString();
                } else if (s
                        .getObject()
                        .stringValue()
                        .equals(SEPA.SEMANTICEVENTCONSUMER
                                .toSesameURI().toString())) {
                    uri = s.getSubject().toString();
                } else if (s
                        .getObject()
                        .stringValue()
                        .equals(SEPA.SEPAINVOCATIONGRAPH
                                .toSesameURI().toString())) {
                    uri = s.getSubject().toString();
                } else if (s
                        .getObject()
                        .stringValue()
                        .equals(SEPA.SECINVOCATIONGRAPH
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
