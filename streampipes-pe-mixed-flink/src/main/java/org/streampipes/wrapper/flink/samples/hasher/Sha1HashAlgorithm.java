package org.streampipes.wrapper.flink.samples.hasher;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha1HashAlgorithm implements HashAlgorithm {

	private static final long serialVersionUID = 1L;

	@Override
	public String toHashValue(Object value) {
		return DigestUtils.shaHex(String.valueOf(value));
	}

}
