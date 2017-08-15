package org.streampipes.pe.mixed.flink.samples.hasher;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5HashAlgorithm implements HashAlgorithm {

	private static final long serialVersionUID = 1L;

	@Override
	public String toHashValue(Object value) {
		return DigestUtils.md5Hex(String.valueOf(value));
	}

}
