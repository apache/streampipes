package org.streampipes.serializers.jsonld;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.empire.pinto.MappingOptions;
import org.streampipes.empire.pinto.RDFMapper;
import org.streampipes.empire.pinto.UriSerializationStrategy;
import org.streampipes.vocabulary.SEPA;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class JsonLdTransformer implements RdfTransformer {

  @Override
  public <T> Graph toJsonLd(T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException {
    return makeRdfMapper()
            .writeValue(element);
  }

  @Override
  public <T> T fromJsonLd(String json, Class<T> destination) throws RDFParseException, UnsupportedRDFormatException, IOException, RepositoryException {

    InputStream stream = new ByteArrayInputStream(
            json.getBytes(StandardCharsets.UTF_8));
    Model statements;
    statements = Rio.parse(stream, "", RDFFormat.JSONLD);
    return makeRdfMapper()
            .readValue(statements, destination, getResource(statements));
  }

  private RDFMapper makeRdfMapper() {
    return RDFMapper
            .builder()
            .set(MappingOptions.IGNORE_PROPERTIES_WITHOUT_ANNOTATION, true)
            .set(MappingOptions.REQUIRE_IDS, true)
            .set(MappingOptions.USE_PROVIDED_CLASSES, new CustomAnnotationProvider())
            .set(MappingOptions.URI_SERIALIZATION_STRATEGY, UriSerializationStrategy.INSTANCE)
            .build();

  }

  private Resource getResource(Model model) {
    Iterator<Statement> st = model.iterator();

    while (st.hasNext()) {
      Statement s = st.next();
      if ((s.getPredicate().equals(RDF.TYPE))) {
        if (isRootElement(s)) {
          return s.getSubject();
        }
      }
    }
    return null;
  }

  private boolean isRootElement(Statement s)  {
    return hasObject(s, SEPA.SEMANTICEVENTPROCESSINGAGENT) ||
            hasObject(s, SEPA.SEMANTICEVENTPRODUCER) ||
            hasObject(s, SEPA.SEMANTICEVENTCONSUMER) ||
            hasObject(s, SEPA.SEPAINVOCATIONGRAPH) ||
            hasObject(s, SEPA.SECINVOCATIONGRAPH);
  }

  private boolean hasObject(Statement statement, SEPA voc) {
    return statement
            .getObject()
            .stringValue()
            .equals(voc.toSesameURI().toString());
  }

}
