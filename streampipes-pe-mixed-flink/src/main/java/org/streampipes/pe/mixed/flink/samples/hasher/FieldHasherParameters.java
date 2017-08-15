package org.streampipes.pe.mixed.flink.samples.hasher;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class FieldHasherParameters extends EventProcessorBindingParams {
	
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
