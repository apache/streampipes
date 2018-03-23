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

package org.streampipes.container.util;

import java.io.IOException;
import java.net.URL;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

public class DeclarerUtils {

	public static <T> T descriptionFromResources(URL resourceUrl, Class<T> destination) throws SepaParseException
	{
		try {
			return new JsonLdTransformer().fromJsonLd(Resources.toString(resourceUrl, Charsets.UTF_8), destination);
		} catch (RDFParseException | UnsupportedRDFormatException
				| RepositoryException | IOException e) {
			e.printStackTrace();
			throw new SepaParseException();
		}
	}
}
