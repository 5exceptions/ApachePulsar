package mq.pulsar;

import org.apache.pulsar.client.api.PulsarClientException;



//Main Class
public class Multithread {
	
 public static void main(String[] args) 
 {
 	
 	MultithreadingDemo demo=new MultithreadingDemo();
		try {
			demo.startInit();
			int totalThread = 10; // Number of threads
			for (int i = 0; i < totalThread; i++) {
				MultithreadingDemo threadObj = new MultithreadingDemo();
				threadObj.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

 
 
}


class MultithreadingDemo extends Thread {

	static String serviceUrl = "pulsar://" + Constant.IP_ADDRESS + ":" + Constant.PORT;
	static String topicName = "test-topic";
	static PulsarProducer pulsarProducer = new PulsarProducer(serviceUrl, topicName);

	@Override
	public void run()
    {
            produce();
        
     
    }
    public void produce() {

        // Displaying the thread that is running
    	  System.out.println(
    	            "Thread " + Thread.currentThread().getId()
    	            + " is running");
    		
    	  for (int i = 1; i <= 50000; i++) {
    			String msg = "Message number " + i;
    			// send message
    			pulsarProducer.send(msg);
    		}
    	  
    	
    	}
    	
    public static void startInit() throws PulsarClientException {
		pulsarProducer.init();
    }
    
    public void close() {
		pulsarProducer.close();
    }
 
}
 
