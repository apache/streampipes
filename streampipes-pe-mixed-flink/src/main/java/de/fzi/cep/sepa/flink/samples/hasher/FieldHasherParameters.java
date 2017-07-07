package de.fzi.cep.sepa.flink.samples.hasher;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

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
