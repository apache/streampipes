package de.fzi.cep.sepa.commons.messaging.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
 
public class KafkaConsumer implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
    
    private IMessageListener<byte[]> listener;
 
    public KafkaConsumer(KafkaStream a_stream, int a_threadNumber, String topic, IMessageListener<byte[]> listener) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
        this.listener = listener;
        
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        for(;;) {
	        if (it.hasNext())
	        {
	        	byte[] msg = it.next().message();
	        	try {
	        		listener.onEvent(msg);
	        	} catch (Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        } else
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        
        //System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
