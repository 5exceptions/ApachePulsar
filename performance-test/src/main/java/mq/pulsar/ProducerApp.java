package mq.pulsar;

import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.ProducerStatsRecorderImpl;
import org.slf4j.*;

/*
 * 
 * EXAMPLE  : Pulsar performance testing 
 * Reference Link : https://pulsar.apache.org/docs/reference-cli-tools/#pulsar-perf
 * 
 */

class ProducerApp {
	
	private static final Logger logger = LoggerFactory.getLogger(ProducerApp.class);

	// Entry point of the application
	public static void main(String[] args) throws PulsarClientException {

		String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
		String topicName = "perf-test-topic";

		// init
		PulsarProducer pulsarProducer = new PulsarProducer(serviceUrl, topicName);
		pulsarProducer.init();

		long start=System.currentTimeMillis();
		logger.info("Start Producing : {}", start);

		// Send messages to queue 
		int totalMsgCount = 200000;
		
		totalMsgCount = 5;
		
		for (int i = 1; i <= totalMsgCount; i++) {
			String msg = "Message number " + i;
			// send message
			pulsarProducer.send(msg);
		}
		logger.info("End Producing : {}", System.currentTimeMillis()-start);
				
		// Close 
	//pulsarProducer.close();
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
			    .sendTimeout(10, TimeUnit.SECONDS)
			    .blockIfQueueFull(true)
			    .enableBatching(true)
			    .batchingMaxPublishDelay(2, TimeUnit.MILLISECONDS)
			    .batchingMaxMessages(5000)
			    .create();
		
		// 		.batchingMaxBytes(1024 * 1024)
		
	}

	// Send or publish message to message queue
	void send(String msg)   {

		byte[] msgBytes = msg.getBytes();
		
			mqProducer.sendAsync(msgBytes)
			.thenAccept(msgId -> 
							logger.info("Producer has sent message ID : {} with Message : {}", msgId ,msg));
	
	}

	void close()  {
		try {
		
			ProducerStatsRecorderImpl producerStatsRecorderImpl=new ProducerStatsRecorderImpl();
			
			logger.info("TotalMsgsSent : {}", producerStatsRecorderImpl.getTotalMsgsSent());
			logger.info("SendLatencyMillisMax : {}", producerStatsRecorderImpl.getSendLatencyMillisMax());
			logger.info("SendMsgsRate : {}", producerStatsRecorderImpl.getSendMsgsRate());
			logger.info("TotalAcksReceived : {}", producerStatsRecorderImpl.getTotalAcksReceived());
			logger.info("TotalBytesSent : {}", producerStatsRecorderImpl.getTotalBytesSent());
			logger.info("SendLatencyMillisMax : {}", producerStatsRecorderImpl.getSendLatencyMillisMax());

			mqProducer.close();
			
		} catch (PulsarClientException e) {
			logger.info("Producer Exception while closing connection : {}", e.getMessage());

		}
		logger.info("Producer is closed : {}",System.currentTimeMillis());
	}

	// Private
	private PulsarClient getPulsarClient() throws PulsarClientException {
		return PulsarClient.builder()	
				.serviceUrl(mqServiceUrl)
				.build();
		
		// .enableTransaction(true)

	}

}