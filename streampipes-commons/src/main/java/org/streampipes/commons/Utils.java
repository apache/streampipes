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

package org.streampipes.commons;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.helpers.JSONLDMode;
import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

  public static List<URI> createURI(String... uris) {
    List<URI> result = new ArrayList<>();
    for (String uri : uris) {
      result.add(URI.create(uri));
    }
    return result;
  }

  @SafeVarargs
  public static <T> List<T> createList(T... objects) {
    List<T> result = new ArrayList<>();
    Collections.addAll(result, objects);
    return result;
  }

  public static String getRandomString() {
    return RandomStringUtils.randomAlphabetic(10);
  }

  public static String asString(Graph graph) throws RDFHandlerException {
    OutputStream stream = new ByteArrayOutputStream();

    RDFWriter writer = Utils.getRioWriter(stream);

    Rio.write(graph, writer);
    return stream.toString();
  }


  public static RDFWriter getRioWriter(OutputStream stream) throws RDFHandlerException {
    RDFWriter writer = Rio.createWriter(RDFFormat.JSONLD, stream);

    writer.handleNamespace("sp", "https://streampipes.org/vocabulary/v1/");
    writer.handleNamespace("ssn", "http://purl.oclc.org/NET/ssnx/ssn#");
    writer.handleNamespace("xsd", "http://www.w3.org/2001/XMLSchema#");
    writer.handleNamespace("empire", "urn:clarkparsia.com:empire:");
    writer.handleNamespace("spi", "urn:streampipes.org:spi:");

    writer.getWriterConfig().set(JSONLDSettings.JSONLD_MODE, JSONLDMode.COMPACT);
    writer.getWriterConfig().set(JSONLDSettings.OPTIMIZE, true);
    writer.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, true);

    return writer;
  }

}
