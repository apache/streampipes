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
package com.github.jqudt.onto;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.io.InputStream;

public class OntoReader {

	protected static void read(Model repos, String ontology)
			throws RDFParseException, IOException {
		String filename = "onto/" + ontology;
		InputStream ins = OntoReader.class.getClassLoader()
				.getResourceAsStream(filename);

		if(filename.endsWith(".ttl")) {
			repos.addAll(Rio.parse(ins, "",RDFFormat.TURTLE));
		} else {
			repos.addAll(Rio.parse(ins, "",RDFFormat.RDFXML));
		}

	}
}
