package org.streampipes.wrapper.spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.KafkaTransportProtocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2017-12-07.
 */
public abstract class SparkRuntime<I extends InvocableStreamPipesEntity> implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;

    protected Thread thread;
    protected SparkAppHandle appHandle;
    protected SparkLauncher launcher;
    protected JavaStreamingContext streamingContext;//TODO: static wieder raus nach Aufteilung
    protected I graph;
    protected Map kafkaParams;

    public SparkRuntime(I graph) {
        this.graph = graph;

        kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "kafka:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
    }
    //TODO: Constructor(en)

    /*
    public static void main(String[] args) {
        //TODO: wie startExecution() sinnvoll verteilen
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "kafka:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("topicA", "topicB");

        final JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );

        stream.mapToPair(new PairFunction<ConsumerRecord<String, String>, String, String>() {
                    @Override
                    public Tuple2<String, String> call(ConsumerRecord<String, String> record) {
                        return new Tuple2<>(record.key(), record.value());
                    }
        });
    }*/

    public boolean startExecution() {
        launcher = new SparkLauncher()
                .setAppResource("/home/lutz/BigGIS/sparktest1/target/spark-test1-0.1-SNAPSHOT.jar")//TODO
                .setMainClass("org.streampipes.biggis.sparktest1.SparkTest1Program")//TODO
                .setMaster("local[*]")//TODO
                .setConf(SparkLauncher.DRIVER_MEMORY, "2g");//TODO

        SparkConf conf = new SparkConf().setAppName("Spark-Test-1")//TODO
                .setMaster("local[*]");//TODO
        streamingContext = new JavaStreamingContext(conf, new Duration(1000));//TODO: millis aus Consul/sinnvoller Default


        //TODO: Error Handling



		return true;
	}

	public abstract boolean execute(JavaDStream<Map<String, Object>>[] convertedStream); //TODO: richtiger Spark-Stream-Typ (passt DStream? Ist das Parent der anderen in oas.streaming.dstream?)

    public void run() {

		try {
            appHandle = launcher.startApplication();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean stop() {
        //TODO: vermutlich via appHandle
		//FlinkJobController ctrl = new FlinkJobController(config.getHost(), config.getPort());//TODO: das ist noch Flink-spezifisch
		try {
			//return ctrl.deleteJob(ctrl.findJobId(ctrl.getJobManagerGateway(), graph.getElementId()));//TODO: das ist noch Flink-spezifisch
            return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
    private JavaInputDStream<ConsumerRecord<String, String>> getStream1Source() {
        return getStreamSource(0);
    }

    private JavaInputDStream<ConsumerRecord<String, String>> getStream2Source() {
        return getStreamSource(1);
    }

    /**
     * This method takes the i's input stream and creates a source for the flink graph
     * Currently just kafka is supported as a protocol
     * TODO Add also jms support
     * @param i
     * @return
     */
    private JavaInputDStream<ConsumerRecord<String, String>> getStreamSource(int i) {
        if (graph.getInputStreams().size() - 1 >= i) {

            SpDataStream stream = graph.getInputStreams().get(i);
            if (stream != null) {
                KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();

                return KafkaUtils.createDirectStream(streamingContext,LocationStrategies.PreferConsistent(),ConsumerStrategies.<String, String>Subscribe(Arrays.asList(protocol.getTopicName()), kafkaParams));
            } else {
                return null;
            }
        }
        else {
            return null;
        }
    }
}
