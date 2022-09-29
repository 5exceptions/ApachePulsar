package mq.pulsar;

import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.*;
import org.slf4j.*;
/*
 * 
 * EXAMPLE   : Simple Producer and Consumer 
 * 			 : Implemented DE-DUPLICATION (Check note 1, note 2, note 3)
 *           : Configuration   
 *             message de duplication can be enabled at 
 *             - broker level, 
 *             - for a specific namespace or topic  *             
 *             
 * 
 * # Reference Link : https://pulsar.apache.org/docs/2.3.1/concepts-messaging/#message-deduplication
 * 					  https://pulsar.apache.org/docs/2.3.1/cookbooks-deduplication/
 *  
 */

public class ProducerApp {

	// Entry point of the application
	public static void main(String[] args) throws PulsarClientException {

		String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
		String topicName = "test-topic-deduplication";

		// init
		PulsarProducer pulsarProducer = new PulsarProducer(serviceUrl, topicName);
		pulsarProducer.init();
		
		
		int counter = 5;
		// Send 5 messages to queue 
		for (int i = 1; i <= 5; i++) {
			int sequenceNum = i + counter;
			String msg = "Deduplication Message Number " + sequenceNum;
			// send message
			pulsarProducer.send(msg, sequenceNum);
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
		
		// NOTE 1 --------------------- SET PRODUCER NAME ------------------------
		// NOTE 2 --------------------- SET sendTimeout ------------------------
		
		// Create producer object 
		mqProducer = getPulsarClient().newProducer()
				.producerName("producer-1")
				.topic(mqTopicName)
				.compressionType(CompressionType.LZ4)
				.sendTimeout(0, TimeUnit.SECONDS)
				.create();
	}

	@SuppressWarnings("deprecation")
	// Send or publish message to message queue
	void send(String msg, int seqId) {

		logger.info("Producer is sending message: {}", msg);

		try {
			
			// NOTE 3 --------------------- send message ------------------------
			Message message = MessageBuilder.create()
					.setSequenceId(seqId)
					.setContent(msg.getBytes())
					.build();
			MessageId msgId = mqProducer.send(message);		
			
			/*
			MessageId msgId = mqProducer.newMessage()
					.sequenceId(seqId)
					.value(msg.getBytes())
					.send();
			*/		

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
