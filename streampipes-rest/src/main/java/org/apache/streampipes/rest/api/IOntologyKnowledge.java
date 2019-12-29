/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.rest.api;

import org.apache.streampipes.model.client.ontology.Concept;
import org.apache.streampipes.model.client.ontology.Instance;
import org.apache.streampipes.model.client.ontology.Namespace;
import org.apache.streampipes.model.client.ontology.Property;
import org.apache.streampipes.model.client.ontology.Resource;

import javax.ws.rs.core.Response;

public interface IOntologyKnowledge {

	Response getPropertyHierarchy();

	Response getProperty(String propertyId);

	Response addProperty(Resource elementData);

	Response updateProperty(String propertyId, Property propertyData);

	Response deleteProperty(String propertyId);


	Response getTypeHiearchy();

	Response getType(String typeId);

	Response addType(Resource elementData);

	Response updateType(String typeId, Concept elementData);

	Response deleteType(String typeId);


	Response addInstance(Resource elementData);

	Response getInstance(String instanceId);

	Response updateInstance(String instanceId, Instance elementData);

	Response deleteInstance(String instanceId);
	
	
	Response getNamespaces();

	Response addNamespace(Namespace namespace);
	
	Response deleteNamespace(String prefix);

	
	
}
