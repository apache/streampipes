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

package org.streampipes.model.grounding;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.RDF;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.TRANSPORT_FORMAT)
@Entity
public class TransportFormat extends UnnamedStreamPipesEntity {
	
	private static final long serialVersionUID = -525073244975968386L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(RDF.TYPE)
	private List<URI> rdfType;

	public TransportFormat()
	{
		super();
		this.rdfType = new ArrayList<>();
	}

	public TransportFormat(String transportFormatType)
	{
		super();
		this.rdfType = new ArrayList<>();
		this.rdfType.add(URI.create(transportFormatType));
	}
	
	public TransportFormat(TransportFormat other)
	{
		super(other);
		this.rdfType = other.getRdfType();
	}

	public List<URI> getRdfType() {
		return rdfType;
	}

	public void setRdfType(List<URI> rdfType) {
		this.rdfType = rdfType;
	}

}
