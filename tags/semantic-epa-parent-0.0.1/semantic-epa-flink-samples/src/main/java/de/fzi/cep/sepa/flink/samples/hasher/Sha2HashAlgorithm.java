package de.fzi.cep.sepa.flink.samples.hasher;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha2HashAlgorithm implements HashAlgorithm {

	@Override
	public String toHashValue(Object value) {
		return DigestUtils.sha256Hex(String.valueOf(value));
	}

}
