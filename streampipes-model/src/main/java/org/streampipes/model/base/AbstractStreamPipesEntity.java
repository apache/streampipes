package org.streampipes.model.base;


import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.empire.core.empire.annotation.SupportsRdfIdImpl;
import org.streampipes.vocabulary.RDF;
import org.streampipes.vocabulary.RDFS;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.SSN;
import org.streampipes.vocabulary.StreamPipes;

import java.io.Serializable;


/**
 * top-level StreamPipes element
 */

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS,
				"dc",   "http://purl.org/dc/terms/", RDFS.NS_PREFIX, RDFS.NS, RDF.NS_PREFIX, RDF.NS, SO.NS_PREFIX, SO.NS,
				SSN.NS_PREFIX, SSN.NS})
public class AbstractStreamPipesEntity implements SupportsRdfId, Serializable {

	private static final long serialVersionUID = -8593749314663582071L;

	private transient SupportsRdfIdImpl myId;
	
	AbstractStreamPipesEntity()
	{
		myId = new SupportsRdfIdImpl();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public RdfKey getRdfId() {
		return myId.getRdfId();
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void setRdfId(RdfKey arg0) {
		myId.setRdfId(arg0);	
	}

}
