package mq.pulsar;

import org.apache.pulsar.client.api.PulsarClientException;

public interface MessageListener {
  void messageFetched(String msg) throws PulsarClientException;
}

