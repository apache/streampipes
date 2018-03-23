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

package org.streampipes.manager.verification.extractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import org.streampipes.commons.exceptions.SepaParseException;

public class StatementBuilder {

	public static Model extractStatements(String graphData) throws SepaParseException
	{
		try {
			return Rio.parse(getGraphDataAsStream(graphData), "", RDFFormat.JSONLD);
		} catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
			throw new SepaParseException();
		}
	}
	
	private static InputStream getGraphDataAsStream(String graphData)
	{
		InputStream stream = new ByteArrayInputStream(
				graphData.getBytes(StandardCharsets.UTF_8));
		
		return stream;
	}
}
