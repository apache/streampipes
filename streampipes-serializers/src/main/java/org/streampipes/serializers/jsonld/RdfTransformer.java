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
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public interface RdfTransformer {

	<T> Graph toJsonLd(T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException;
	
	<T> T fromJsonLd(String json, Class<T> destination) throws RDFParseException, UnsupportedRDFormatException, IOException, RepositoryException;
	
}
