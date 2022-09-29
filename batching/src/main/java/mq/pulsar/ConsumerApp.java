package mq.pulsar;

import org.apache.pulsar.client.api.*;
import org.slf4j.*;

import java.util.ArrayList;

public class ConsumerApp {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerApp.class);
	
	public static void main(String[] args) throws PulsarClientException {
		
		String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
		String topicName = "test-topic-batching";
		
		ArrayList<String> msgList = new ArrayList<>();		

		PulsarConsumer consumer = new PulsarConsumer(serviceUrl, topicName);
		consumer.addListener(msg -> {
			msgList.add(msg);

			if (msgList.size() >= 10) {
				logger.info(msgList.toString());

				consumer.close();
			}
		});
		consumer.run();
	}

}

class PulsarConsumer {

	// Variables
	private final String mqServiceUrl;
	private final String mqTopicName;
	private Consumer<byte[]> mqConsumer;
	private MessageListener msgListener;
	private static final Logger logger = LoggerFactory.getLogger(PulsarConsumer.class);
	
	// Constructor
	PulsarConsumer(String serviceUrl, String topicName) {
		mqServiceUrl = serviceUrl;
		mqTopicName = topicName;
	}

	// Add Listener
	void addListener(MessageListener listener) {
		msgListener = listener;
	}

	void run() throws PulsarClientException {
		assert msgListener == null;

		logger.info("Initializing consumer...");

		mqConsumer = getPulsarClient()
				.newConsumer()
				.topic(mqTopicName)
				.subscriptionType(SubscriptionType.Shared)				
				.subscriptionName(mqTopicName)
				.messageListener(this::readMessage)
				.subscribe();
	}

	void close() throws PulsarClientException {
		mqConsumer.close();
		logger.info("Consuner is closed");
	}

	// Get Pulsar Client  
	private PulsarClient getPulsarClient() throws PulsarClientException {
		return PulsarClient
				.builder()
				.serviceUrl(mqServiceUrl)
				.build();
	}

	private void readMessage(Consumer<byte[]> consumer, Message msg) {
		try {
			consumer.acknowledge(msg);

			String content = new String(msg.getData());
			msgListener.messageFetched(content);

			logger.info("Consumer: {} ", content);
			
		} catch (PulsarClientException e) {
			logger.error("Exception : Error in reading message : ", e);
		}
	}
}
