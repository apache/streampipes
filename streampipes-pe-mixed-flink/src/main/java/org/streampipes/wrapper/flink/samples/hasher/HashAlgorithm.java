package org.streampipes.wrapper.flink.samples.hasher;

import java.io.Serializable;

public interface HashAlgorithm extends Serializable {

	String toHashValue(Object value);
}
