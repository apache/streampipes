package de.fzi.cep.sepa.flink.source;

import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaConsumer;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by riemer on 01.10.2016.
 */
public class NonParallelKafkaSource implements SourceFunction<String>, EventListener<byte[]> {

    private String kafkaUrl;
    private String topic;
    private StreamPipesKafkaConsumer kafkaConsumer;
    private Queue<String> queue;

    private volatile boolean isRunning = true;

    public NonParallelKafkaSource(String kafkaUrl, String topic) {
        this.kafkaUrl = kafkaUrl;
        this.topic = topic;
        this.kafkaConsumer = openConsumer();
//        this.queue = new ArrayBlockingQueue<>(1000);
        this.queue = new LinkedBlockingQueue<>();
    }

    private StreamPipesKafkaConsumer openConsumer() {
        StreamPipesKafkaConsumer kafkaConsumer = new StreamPipesKafkaConsumer(kafkaUrl, topic, this);
        return kafkaConsumer;
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {

        Thread thread = new Thread(kafkaConsumer);
        thread.start();

        while (isRunning) {
            if (!queue.isEmpty()) {
                sourceContext.collect(queue.poll());
            }
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
        kafkaConsumer.close();
    }


    @Override
    public void onEvent(byte[] event) {
        this.queue.add(new String(event));
    }
}
