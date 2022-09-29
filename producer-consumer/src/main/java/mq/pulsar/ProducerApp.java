package mq.pulsar;

import org.apache.pulsar.client.api.*;
import org.slf4j.*;
/*
 * 
 * EXAMPLE  : Simple Producer and Consumer 
 * Reference Link : https://pulsar.apache.org/docs/concepts-messaging/ 
 * 
 */

public class ProducerApp {

	// Entry point of the application
	public static void main(String[] args) throws PulsarClientException {

		String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
		String topicName = "test-topic";

		// init
		PulsarProducer pulsarProducer = new PulsarProducer(serviceUrl, topicName);
		pulsarProducer.init();

		// Send 5 messages to queue 
		for (int i = 1; i <= 5; i++) {
			String msg = "Message number " + i;
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

	// Constructor
	PulsarProducer(String serviceUrl, String topicName) {
		mqServiceUrl = serviceUrl;
		mqTopicName = topicName;
	}

	// Initialize the producer
	void init() throws PulsarClientException {
		// Create producer object 
		mqProducer = getPulsarClient().newProducer()
				.topic(mqTopicName)
				.create();
	}

	// Send or publish message to message queue
	void send(String msg) {

		logger.info("Producer is sending message: {}", msg);

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
