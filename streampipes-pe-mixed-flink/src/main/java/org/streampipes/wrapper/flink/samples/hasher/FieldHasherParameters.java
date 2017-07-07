package org.streampipes.wrapper.flink.samples.hasher;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public class FieldHasherParameters extends BindingParameters {
	
	private String propertyName;
	private HashAlgorithmType hashAlgorithmType;
	
	public FieldHasherParameters(SepaInvocation graph, String propertyName, HashAlgorithmType hashAlgorithmType) {
		super(graph);
		this.propertyName = propertyName;
		this.hashAlgorithmType = hashAlgorithmType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public HashAlgorithmType getHashAlgorithmType() {
		return hashAlgorithmType;
	}
	
}
