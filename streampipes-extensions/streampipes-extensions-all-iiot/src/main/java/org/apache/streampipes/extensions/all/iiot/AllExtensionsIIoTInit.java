package org.apache.streampipes.extensions.all.iiot;

import org.apache.streampipes.connect.iiot.ConnectAdapterIiotInit;
import org.apache.streampipes.container.extensions.ExtensionsModelSubmitter;
import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.container.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.apache.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.processors.changedetection.jvm.ChangeDetectionJvmInit;
import org.apache.streampipes.processors.enricher.jvm.EnricherJvmInit;
import org.apache.streampipes.processors.filters.jvm.FiltersJvmInit;
import org.apache.streampipes.processors.siddhi.FiltersSiddhiInit;
import org.apache.streampipes.processors.transformation.jvm.TransformationJvmInit;
import org.apache.streampipes.sinks.brokers.jvm.BrokersJvmInit;
import org.apache.streampipes.sinks.databases.jvm.DatabasesJvmInit;
import org.apache.streampipes.sinks.internal.jvm.SinksInternalJvmInit;
import org.apache.streampipes.sinks.notifications.jvm.SinksNotificationsJvmInit;

public class AllExtensionsIIoTInit extends ExtensionsModelSubmitter {

  public static void main (String[] args) {
    new AllExtensionsIIoTInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org.apache.streampipes.extensions.all.iiot",
        "StreamPipes Extensions (IIoT only)",
        "", 8090)
      .merge(new ConnectAdapterIiotInit().provideServiceDefinition())
      .merge(new SinksInternalJvmInit().provideServiceDefinition())
      .merge(new FiltersJvmInit().provideServiceDefinition())
      .merge(new ChangeDetectionJvmInit().provideServiceDefinition())
      .merge(new EnricherJvmInit().provideServiceDefinition())
      .merge(new FiltersSiddhiInit().provideServiceDefinition())
      .merge(new TransformationJvmInit().provideServiceDefinition())
      .merge(new BrokersJvmInit().provideServiceDefinition())
      .merge(new DatabasesJvmInit().provideServiceDefinition())
      .merge(new SinksNotificationsJvmInit().provideServiceDefinition())
      .registerMessagingFormats(
        new JsonDataFormatFactory(),
        new CborDataFormatFactory(),
        new SmileDataFormatFactory(),
        new FstDataFormatFactory())
      .registerMessagingProtocols(
        new SpKafkaProtocolFactory(),
        new SpJmsProtocolFactory(),
        new SpMqttProtocolFactory())
      .build();
  }
}
