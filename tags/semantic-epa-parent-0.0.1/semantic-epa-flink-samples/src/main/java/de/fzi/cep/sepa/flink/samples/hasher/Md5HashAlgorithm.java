package de.fzi.cep.sepa.flink.samples.hasher;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5HashAlgorithm implements HashAlgorithm {

	@Override
	public String toHashValue(Object value) {
		return DigestUtils.md5Hex(String.valueOf(value));
	}

}
