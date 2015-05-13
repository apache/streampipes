package de.fzi.cep.sepa.sources.samples.util;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
 
public class KafkaConsumer implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
    
    private ActiveMQPublisher publisher;
 
    public KafkaConsumer(KafkaStream a_stream, int a_threadNumber, String topic) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
        publisher = new ActiveMQPublisher(topic);
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext())
        {
           System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
            publisher.send(new String(it.next().message()));
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
