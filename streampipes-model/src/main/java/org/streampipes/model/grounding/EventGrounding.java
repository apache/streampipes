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
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass(StreamPipes.EVENT_GROUNDING)
@Entity
public class EventGrounding extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 3149070517282698799L;

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_TRANSPORT_PROTOCOL)
	private List<TransportProtocol> transportProtocols;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_TRANSPORT_FORMAT)
	private List<TransportFormat> transportFormats;
	
	public EventGrounding()
	{
		super();
		this.transportFormats = new ArrayList<>();
	}
	
	public EventGrounding(TransportProtocol transportProtocol, TransportFormat transportFormat)
	{
		this();
		this.transportFormats = new ArrayList<>();
		this.transportFormats.add(transportFormat);
		this.transportProtocols = Arrays.asList(transportProtocol);
	}

	public EventGrounding(EventGrounding other) {
		super(other);
		this.transportProtocols = new Cloner().protocols(other.getTransportProtocols());
		this.transportFormats = new Cloner().transportFormats(other.getTransportFormats());
	}

	public List<TransportProtocol> getTransportProtocols() {
		return transportProtocols;
	}

	public void setTransportProtocols(List<TransportProtocol> transportProtocols) {
		this.transportProtocols = transportProtocols;
	}

	public void setTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocols = Collections.singletonList(transportProtocol);
	}
	
	public TransportProtocol getTransportProtocol() {
		return transportProtocols.get(0);
	}
	
	public List<TransportFormat> getTransportFormats() {
		return transportFormats;
	}

	public void setTransportFormats(List<TransportFormat> transportFormats) {
		this.transportFormats = transportFormats;
	}
	
}
