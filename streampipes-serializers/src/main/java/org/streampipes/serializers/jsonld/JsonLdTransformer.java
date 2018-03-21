package org.streampipes.serializers.jsonld;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
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
import org.streampipes.model.base.Namespaces;
import org.streampipes.vocabulary.StreamPipes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
            .set(MappingOptions.REGISTER_ADDITIONAL_NAMESPACES, Arrays.asList(new SimpleNamespace("sp",
                    Namespaces.SP), new SimpleNamespace("so", Namespaces.SO)))
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
    return hasObject(s, StreamPipes.DATA_PROCESSOR_DESCRIPTION) ||
            hasObject(s, StreamPipes.DATA_SOURCE_DESCRIPTION) ||
            hasObject(s, StreamPipes.DATA_SINK_DESCRIPTION) ||
            hasObject(s, StreamPipes.DATA_PROCESSOR_INVOCATION) ||
            hasObject(s, StreamPipes.DATA_SINK_INVOCATION) ||
            hasObject(s, StreamPipes.FORMAT_DESCRIPTION_LIST) ||
            hasObject(s, StreamPipes.PROTOCOL_DESCRIPTION_LIST) ||
            hasObject(s, StreamPipes.ADAPTER_DESCRIPTION);
  }

  private boolean hasObject(Statement statement, String voc) {
    return statement
            .getObject()
            .stringValue()
            .equals(voc);
  }

}
