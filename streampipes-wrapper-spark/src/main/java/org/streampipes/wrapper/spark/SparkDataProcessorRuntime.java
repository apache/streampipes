package org.streampipes.wrapper.spark;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.spark.serializer.SimpleKafkaSerializer;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Base64;
import java.util.Map;

/**
 * Created by Jochen Lutz on 2017-12-07.
 */
public abstract class SparkDataProcessorRuntime<B extends EventProcessorBindingParams> extends SparkRuntime<DataProcessorInvocation> {
    private static final long serialVersionUID = 1L;
    protected B params;

    public SparkDataProcessorRuntime(B params, SparkDeploymentConfig deploymentConfig) {
        super(params.getGraph(), deploymentConfig);
        this.params = params;
    }

    protected abstract JavaDStream<Map<String, Object>> getApplicationLogic(JavaDStream<Map<String, Object>>... messageStream);

    @Override
    public boolean execute(JavaDStream<Map<String, Object>>... convertedStream) {
        JavaDStream<Map<String, Object>> applicationLogic = getApplicationLogic(convertedStream);
        //applicationLogic.print();

        if (isOutputKafkaProtocol()) {
            applicationLogic.foreachRDD(SimpleKafkaSerializer.getInstance(kafkaParams, protocol().getTopicName()));
        }
        else {
            //TODO: JMS
        }

        thread = new Thread(this);
        thread.start();

        return true;
    }

    /**
     * Serialize the instance's data to transmit to Spark cluster.
     * The serialized data contains
     *   - the class name (as String) of the job's program class (inheriting from this class)
     *   - the class name (as String) of the job's parameter class (inheriting from EventProcessorBindingParams),
     *     stored in this.params
     *   - this.params
     *   - this.deplomentconfig
     *
     * This data is deserialized in SparkDataProcessorRuntime.main()
     * @return the serialized data as byte array
     */
    @Override
    protected byte[] getSerializationData() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);

            out.writeObject(this.getClass().getName());
            out.writeObject(params.getClass().getName());
            out.writeObject(params);
            out.writeObject(deploymentConfig);

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

    return bos.toByteArray();
    }

    private boolean isOutputKafkaProtocol() {
        return protocol() instanceof KafkaTransportProtocol;
    }

    private TransportProtocol protocol() {
        return params
                .getGraph()
                .getOutputStream()
                .getEventGrounding()
                .getTransportProtocol();
    }

    /**
     * Entry point for execution on Spark clusters.
     * @param args
     * args[0] contains the serialized and base64-encoded data of the instance to run.
     */
    public static void main(String[] args) {
        String enc = args[0];
        //System.out.println("Received data: '" + enc + "'");

        byte[] data = Base64.getDecoder().decode(enc);

        String programClassName = null;
        String paramsClassName = null;
        EventProcessorBindingParams params = null;
        SparkDeploymentConfig sparkDeploymentConfig = null;

        InputStream fis = null;
        fis = new ByteArrayInputStream(data);
        ObjectInputStream o = null;
        try {
            o = new ObjectInputStream(fis);
            programClassName = (String) o.readObject();
            paramsClassName = (String) o.readObject();
            params = (EventProcessorBindingParams) o.readObject();

            sparkDeploymentConfig = (SparkDeploymentConfig) o.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (programClassName != null && paramsClassName != null && params != null && sparkDeploymentConfig != null) {
            try {
                Class<?> programClass = Class.forName(programClassName);
                Class<?> paramsClass = Class.forName(paramsClassName);

                Class[] types = new Class[]{paramsClass, SparkDeploymentConfig.class};
                Constructor constructor = programClass.getConstructor(types);

                Object[] parameters = {params, sparkDeploymentConfig};
                SparkDataProcessorRuntime prog = (SparkDataProcessorRuntime) constructor.newInstance(parameters);

                sparkDeploymentConfig.setRunLocal(true);

                prog.startExecution();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}