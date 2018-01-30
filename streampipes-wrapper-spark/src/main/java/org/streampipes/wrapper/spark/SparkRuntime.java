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
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.spark.converter.JsonToMapFormat;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2017-12-07.
 */
public abstract class SparkRuntime<I extends InvocableStreamPipesEntity> implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;
    protected final SparkDeploymentConfig deploymentConfig;

    protected Thread thread;
    protected SparkAppHandle appHandle;
    protected SparkLauncher launcher;
    protected JavaStreamingContext streamingContext;
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

    public boolean startExecution() {
        if (this.deploymentConfig.isRunLocal()) {
            try {
                SparkConf conf = new SparkConf().setAppName(this.deploymentConfig.getAppName())
                        .setMaster(this.deploymentConfig.getSparkHost());
                streamingContext = new JavaStreamingContext(conf, new Duration(this.deploymentConfig.getSparkBatchDuration()));

                JavaDStream<Map<String, Object>> messageStream1 = null;
                JavaInputDStream<ConsumerRecord<String, String>> source1 = getStream1Source(streamingContext);
                if (source1 != null) {
                    messageStream1 = source1.flatMap(new JsonToMapFormat());
                } else {
                    throw new Exception("At least one source must be defined for a Spark SEPA");
                }
                //TODO: zweiter Stream. Kann das Spark?


                //TODO: Error Handling

                execute(messageStream1);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            try {
                byte[] data = getSerializationData();
                String enc = Base64.getEncoder().encodeToString(data);
                //System.out.println("Sending data: '" + enc + "'");

                launcher = new SparkLauncher()
                        .setAppResource(this.deploymentConfig.getJarFile())
                        .setMainClass(this.getClass().getName())
                        .addAppArgs(enc)
                        //.redirectError()
                        //.redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .setMaster(this.deploymentConfig.getSparkHost())
                        .setConf(SparkLauncher.DRIVER_MEMORY, "2g");//TODO
                appHandle = launcher.startApplication();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    protected abstract byte[] getSerializationData();

    public abstract boolean execute(JavaDStream<Map<String, Object>>... convertedStream);

    public void run() {
        try {
            streamingContext.start();
            if (deploymentConfig.isRunLocal()) {
                streamingContext.awaitTermination();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean stop() {
        try {
            appHandle.stop();
            //streamingContext.stop();
            //streamingContext.awaitTermination();
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
     * This method takes the i's input stream and creates a source for the Spark streaming job
     * Currently just kafka is supported as a protocol
     * TODO Add also jms support
     *
     * @param i
     * @param streamingContext
     * @return
     */
    private JavaInputDStream<ConsumerRecord<String, String>> getStreamSource(int i, JavaStreamingContext streamingContext) {
        if (graph.getInputStreams().size() - 1 >= i) {

            SpDataStream stream = graph.getInputStreams().get(i);
            if (stream != null) {
                KafkaTransportProtocol protocol = (KafkaTransportProtocol) stream.getEventGrounding().getTransportProtocol();

                //System.out.println("Listening on Kafka topic '" + protocol.getTopicName() + "'");
                return KafkaUtils.createDirectStream(streamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.<String, String>Subscribe(Arrays.asList(protocol.getTopicName()), kafkaParams));
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }
}
