package de.fzi.cep.sepa.flink.samples.hasher;

public interface HashAlgorithm {

	public String toHashValue(Object value);
}
