package de.fzi.cep.sepa.flink.samples.hasher;

public enum HashAlgorithmType {
	MD5(new Md5HashAlgorithm()), SHA1(new Sha1HashAlgorithm()), SHA2(new Sha2HashAlgorithm());

	private HashAlgorithm hashAlgorithm;
	
	HashAlgorithmType(HashAlgorithm hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}
	
	public HashAlgorithm hashAlgorithm() {
		return hashAlgorithm;
	}
}
