package de.fzi.cep.sepa.flink.samples.hasher;

import java.io.Serializable;

public interface HashAlgorithm extends Serializable {

	public String toHashValue(Object value);
}
