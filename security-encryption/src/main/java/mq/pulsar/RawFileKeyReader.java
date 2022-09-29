package mq.pulsar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.pulsar.client.api.CryptoKeyReader;
import org.apache.pulsar.client.api.EncryptionKeyInfo;

class RawFileKeyReader implements CryptoKeyReader {

	String publicKeyFile = "";
	String privateKeyFile = "";

	RawFileKeyReader(String pubKeyFile, String privKeyFile) {
		publicKeyFile = pubKeyFile;
		privateKeyFile = privKeyFile;
	}

	@Override
	public EncryptionKeyInfo getPublicKey(String keyName, Map<String, String> keyMeta) {
		EncryptionKeyInfo keyInfo = new EncryptionKeyInfo();
		try {
			keyInfo.setKey(Files.readAllBytes(Paths.get(publicKeyFile)));
		} catch (IOException e) {
			System.err.println("ERROR: Failed to read public key from file " + publicKeyFile);
			e.printStackTrace();
		}
		return keyInfo;
	}

	@Override
	public EncryptionKeyInfo getPrivateKey(String keyName, Map<String, String> keyMeta) {
		EncryptionKeyInfo keyInfo = new EncryptionKeyInfo();
		try {
			keyInfo.setKey(Files.readAllBytes(Paths.get(privateKeyFile)));
		} catch (IOException e) {
			System.err.println("ERROR: Failed to read private key from file " + privateKeyFile);
			e.printStackTrace();
		}
		return keyInfo;
	}
}

