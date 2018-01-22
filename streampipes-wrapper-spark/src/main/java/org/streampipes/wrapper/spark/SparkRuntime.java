package org.streampipes.wrapper.spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
import org.spark_project.guava.base.Strings;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.wrapper.spark.converter.JsonToMapFormat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2017-12-07.
 */
public abstract class SparkRuntime<I extends InvocableStreamPipesEntity> implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;
    private final SparkDeploymentConfig deploymentConfig;

    protected Thread thread;
    protected SparkAppHandle appHandle;
    //protected SparkLauncher launcher;
    protected JavaStreamingContext streamingContext;//TODO: static wieder raus nach Aufteilung
    protected I graph;
    protected Map kafkaParams;

    public SparkRuntime(I graph, SparkDeploymentConfig deploymentConfig) {
        this.graph = graph;
        this.deploymentConfig = deploymentConfig;

        kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", this.deploymentConfig.getKafkaHost());
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("key.serializer", StringSerializer.class);
        kafkaParams.put("value.serializer", StringSerializer.class);
        kafkaParams.put("group.id", "use_a_separate_group_id_for_each_stream");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
    }
    //TODO: Constructor(en)

    /*
    public static void main(String[] args) {
        //TODO: wie startExecution() sinnvoll verteilen



        stream.mapToPair(new PairFunction<ConsumerRecord<String, String>, String, String>() {
                    @Override
                    public Tuple2<String, String> call(ConsumerRecord<String, String> record) {
                        return new Tuple2<>(record.key(), record.value());
                    }
        });
    }*/

    public boolean startExecution() {
        try {
            //TODO: ist der ganze Launcher unnötig? Wie ist das mit Launcher und Streaming?
            /*launcher = new SparkLauncher()
                    .setAppResource("/home/lutz/BigGIS/sparktest1/target/spark-test1-0.1-SNAPSHOT.jar")//TODO
                    .setMainClass("org.streampipes.biggis.sparktest1.SparkTest1Program")//TODO
                    .setMaster("local[*]")//TODO
                    .setConf(SparkLauncher.DRIVER_MEMORY, "2g");//TODO
*/
            SparkConf conf = new SparkConf().setAppName(this.deploymentConfig.getAppName())
                    .setMaster(this.deploymentConfig.getSparkHost());
            streamingContext = new JavaStreamingContext(conf, new Duration(this.deploymentConfig.getSparkBatchDuration()));

            JavaDStream<Map<String, Object>> messageStream1 = null;
            JavaInputDStream<ConsumerRecord<String, String>> source1 = getStream1Source(streamingContext);
            if (source1 != null) {
                messageStream1 = source1.flatMap(new JsonToMapFormat());
            }
            else {
                throw new Exception("At least one source must be defined for a Spark SEPA");
            }
            //TODO: zweiter Stream. Kann das Spark?

            execute(messageStream1);




            //TODO: Error Handling


            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

	public abstract boolean execute(JavaDStream<Map<String, Object>>... convertedStream); //TODO: richtiger Spark-Stream-Typ (passt DStream? Ist das Parent der anderen in oas.streaming.dstream?)

    public void run() {

		try {
		    //TODO: brauche ich hier doch startApplication() und und streamingContext.start() in main()? Wie dann die Daten in main() kriegen?
            //appHandle = launcher.startApplication();
            streamingContext.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean stop() {
        //TODO: vermutlich via appHandle
		try {
		    streamingContext.stop();
		    streamingContext.awaitTermination();
            return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
    private JavaInputDStream<ConsumerRecord<String, String>> getStream1Source(JavaStreamingContext streamingContext) {
        return getStreamSource(0, streamingContext);
    }

    private JavaInputDStream<ConsumerRecord<String, String>> getStream2Source(JavaStreamingContext streamingContext) {
        return getStreamSource(1, streamingContext);
    }

    /**
     * This method takes the i's input stream and creates a source for the flink graph
     * Currently just kafka is supported as a protocol
     * TODO Add also jms support
     * @param i
     * @param streamingContext
     * @return
     */
    private JavaInputDStream<ConsumerRecord<String, String>> getStreamSource(int i, JavaStreamingContext streamingContext) {
        if (graph.getInputStreams().size() - 1 >= i) {

            SpDataStream stream = graph.getInputStreams().get(i);
            if (stream != null) {
                KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();

                System.out.println("Listening on Kafka topic '" + protocol.getTopicName() + "'");
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
