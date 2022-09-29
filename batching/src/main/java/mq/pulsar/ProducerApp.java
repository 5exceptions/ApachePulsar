package mq.pulsar;

import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.*;
import org.slf4j.*;
/*
 * 
 * EXAMPLE   : Simple Producer and Consumer with BATCHING
 * 			 : When batching is enabled, the producer accumulates and sends a batch of messages in a single request.  
 * 			 : Messages are grouped into small batches to achieve some of the performance advantages of batch processing 
 *           : Configuration  
 *             
 * 
 * # Reference Link : https://pulsar.apache.org/docs/concepts-messaging/#batching
 */

public class ProducerApp {

	// Entry point of the application
	public static void main(String[] args) throws PulsarClientException {

		String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
		String topicName = "test-topic-batching";

		// init
		PulsarProducer pulsarProducer = new PulsarProducer(serviceUrl, topicName);
		pulsarProducer.init();

		// Send 5 messages to queue 
		for (int i = 1; i <= 5; i++) {
			String msg = "Batching Message number " + i;
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
				.producerName("test-producer")
				.enableBatching(true)
			    .batchingMaxMessages(100)
			    .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) 
				.create();
		
		
		// 	.batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS) 
		
		// Note : In this example, the producer flushes the batch when the size of the batch exceeds 100 messages.
		// If these parameters are not met within batchingMaxPublishDelay(10 milliseconds), the producer will trigger batch flushing.		
		
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
