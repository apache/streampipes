package org.apache.streampipes.processors.changedetection.jvm;


import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.processors.changedetection.jvm.config.ChangeDetectionJvmConfig;
import org.apache.streampipes.processors.changedetection.jvm.cusum.CusumController;

public class ChangeDetectionJvmInit extends StandaloneModelSubmitter {

    public static void main(String[] args) {
        DeclarersSingleton
                .getInstance()
                .add(new CusumController());

        DeclarersSingleton.getInstance().registerDataFormats(new JsonDataFormatFactory(),
                new CborDataFormatFactory(),
                new SmileDataFormatFactory(),
                new FstDataFormatFactory());

        DeclarersSingleton.getInstance().registerProtocols(new SpKafkaProtocolFactory(),
                new SpJmsProtocolFactory());

        new ChangeDetectionJvmInit().init(ChangeDetectionJvmConfig.INSTANCE);
    }
}
