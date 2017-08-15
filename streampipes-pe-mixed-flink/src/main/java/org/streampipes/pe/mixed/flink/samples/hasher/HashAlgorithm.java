package org.streampipes.pe.mixed.flink.samples.hasher;

import java.io.Serializable;

public interface HashAlgorithm extends Serializable {

	String toHashValue(Object value);
}
