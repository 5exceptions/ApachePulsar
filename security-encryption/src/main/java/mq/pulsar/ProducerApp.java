package mq.pulsar;

import org.apache.pulsar.client.api.*;
import org.slf4j.*;
/*
 * 
 * EXAMPLE  : Simple Producer and Consumer 
 * 			 : Added ENCRYPTION by using public and private key 
 *            
 * AES key is use to encrypt messages(data)
 * Use Public and private key pair to encrypt the AES key(data key),
 * 
 * The producer key is the public key of the key pair, and 
 * the consumer key is the private key of the key pair.
 * 
 * # Reference Link : https://pulsar.apache.org/docs/security-encryption/
 * 
 */



public class ProducerApp {

	// Entry point of the application
	public static void main(String[] args) throws PulsarClientException {

		String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
		String topicName = "test-topic";
		// String topic = "persistent://my-tenant/my-ns/my-topic";		
		
		// Variable used in encryption
		String keyName = "myappkey";
		String pulicKey = "PLEASE_ADD_PATH_OF_YOUR_PUBLIC_KEY.pem FILE";
		String privateKey = "PLEASE_ADD_PATH_OF_YOUR_PRIVATE_KEY.pem FILE";	

		// init
		PulsarProducer pulsarProducer = new PulsarProducer(serviceUrl, topicName, keyName, pulicKey, privateKey);
		pulsarProducer.init();

		
		for (int i = 1; i <= 5; i++) {
			String msg = "Encrpted Message number " + i;
			// send message
			pulsarProducer.send(msg);
		}
		
		// Close 
		pulsarProducer.close();
	}

}

class PulsarProducer {

	private final String mqServiceUrl;
	private final String mqTopicName;
	private Producer<byte[]> mqProducer;
	private final Logger logger = LoggerFactory.getLogger(PulsarProducer.class);

	private String encptionKeyName;
	private CryptoKeyReader keyReader;		
	

	// Constructor
	PulsarProducer(String serviceUrl, String topicName, String keyName, String pulicKey, String privateKey) {
		mqServiceUrl = serviceUrl;
		mqTopicName = topicName;
		encptionKeyName = keyName;
		keyReader = new RawFileKeyReader(pulicKey, privateKey);		
	}

	// Initialize the producer
	void init() throws PulsarClientException {
		// Create producer object 
		mqProducer = getPulsarClient().newProducer()
				.topic(mqTopicName)
				.cryptoKeyReader(keyReader)
				.addEncryptionKey(encptionKeyName)
				.create();		
	}

	// Send or publish message to message queue
	void send(String msg) {
		
		logger.info("Producer is sending encrpted message: {}", msg);

		try {
			byte[] msgBytes = msg.getBytes();
			MessageId msgId = mqProducer.send(msgBytes);
			logger.info("Producer has sent message ID : {}", msgId);
		} catch (PulsarClientException e) {
			logger.error("Exception : Error in sending message : ", e);
		}		
		
	}

	void close() throws PulsarClientException {
		mqProducer.close();
		logger.info("Producer is closed");
	}

	// Private
	private PulsarClient getPulsarClient() throws PulsarClientException {
		return PulsarClient.builder()
				.serviceUrl(mqServiceUrl)
				.build();
	}
}
