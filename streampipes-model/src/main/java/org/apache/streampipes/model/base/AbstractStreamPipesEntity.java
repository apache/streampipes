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

package org.apache.streampipes.model.base;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.core.empire.SupportsRdfId;
import io.fogsy.empire.core.empire.annotation.SupportsRdfIdImpl;
import org.apache.streampipes.model.annotation.TsModel;
import org.apache.streampipes.vocabulary.*;

import java.io.Serializable;


/**
 * top-level StreamPipes element
 */

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS,
				"dc",   "http://purl.org/dc/terms/", RDFS.NS_PREFIX, RDFS.NS, RDF.NS_PREFIX, RDF.NS, SO.NS_PREFIX, SO.NS,
				SSN.NS_PREFIX, SSN.NS})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property="@class")
@JsonSubTypes({
				@JsonSubTypes.Type(NamedStreamPipesEntity.class),
				@JsonSubTypes.Type(UnnamedStreamPipesEntity.class)
})
@TsModel
public class AbstractStreamPipesEntity implements SupportsRdfId, Serializable {

	private static final long serialVersionUID = -8593749314663582071L;

	private transient SupportsRdfIdImpl myId;
	
	AbstractStreamPipesEntity() {
		myId = new SupportsRdfIdImpl();
	}

	@SuppressWarnings("rawtypes")
	@Override
	@JsonIgnore
	public RdfKey getRdfId() {
		return myId.getRdfId();
	}


	@SuppressWarnings("rawtypes")
	@Override
	@JsonIgnore
	public void setRdfId(RdfKey arg0) {
		myId.setRdfId(arg0);	
	}

}
