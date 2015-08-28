package de.fzi.cep.sepa.storm.messaging;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
 
public class KafkaConsumer implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
    
    private IMessageListener listener;
 
    public KafkaConsumer(KafkaStream a_stream, int a_threadNumber, String topic, IMessageListener listener) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
        this.listener = listener;
        
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext())
        {
            listener.onEvent(new String(it.next().message()));
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
