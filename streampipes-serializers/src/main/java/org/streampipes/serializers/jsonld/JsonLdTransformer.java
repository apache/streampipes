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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JsonLdTransformer implements RdfTransformer {

  private static final List<String> standardRootElements = Arrays.asList(StreamPipes.DATA_PROCESSOR_DESCRIPTION,
          StreamPipes.DATA_SOURCE_DESCRIPTION,
          StreamPipes.DATA_SINK_DESCRIPTION,
          StreamPipes.DATA_PROCESSOR_INVOCATION,
          StreamPipes.DATA_SINK_INVOCATION,
          StreamPipes.FORMAT_DESCRIPTION_LIST,
          StreamPipes.PROTOCOL_DESCRIPTION_LIST,
          StreamPipes.GUESS_SCHEMA,
          StreamPipes.DOMAIN_PROPERTY_PROBABILITY_LIST,
//          StreamPipes.ADAPTER_STREAM_DESCRIPTION,
          StreamPipes.GENERIC_ADAPTER_SET_DESCRIPTION,
          StreamPipes.GENERIC_ADAPTER_STREAM_DESCRIPTION,
          StreamPipes.SPECIFIC_ADAPTER_SET_DESCRIPTION,
          StreamPipes.SPECIFIC_ADAPTER_STREAM_DESCRIPTION,
//          StreamPipes.ADAPTER_SET_DESCRIPTION,
          StreamPipes.ADAPTER_DESCRIPTION_LIST

  );

  private List<String> selectedRootElements;

  public JsonLdTransformer() {
    this.selectedRootElements = standardRootElements;
  }

  public JsonLdTransformer(String rootElement) {
    this.selectedRootElements = Collections.singletonList(rootElement);
  }

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

  private boolean isRootElement(Statement s) {
    return selectedRootElements
            .stream()
            .anyMatch(rootElement -> hasObject(s, rootElement));
  }

  private boolean hasObject(Statement statement, String voc) {
    return statement
            .getObject()
            .stringValue()
            .equals(voc);
  }

}
