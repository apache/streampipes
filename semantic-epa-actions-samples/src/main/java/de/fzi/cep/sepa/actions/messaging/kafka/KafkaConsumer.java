package de.fzi.cep.sepa.actions.messaging.kafka;

import de.fzi.cep.sepa.actions.messaging.jms.IMessageListener;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
 
public class KafkaConsumer implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
    
	private IMessageListener listener;
    
    public KafkaConsumer(KafkaStream a_stream, int a_threadNumber, KafkaTopic topic) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
        this.listener = topic.getListener();
       
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext())
        {
           // System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
           listener.onEvent(new String(it.next().message()));
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
