/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.jvm;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.streampipes.dataformat.fst.FstDataFormatFactory;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.dataformat.smile.SmileDataFormatFactory;
import org.streampipes.messaging.jms.SpJmsProtocolFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.pe.jvm.config.AllPipelineElementsConfig;
import org.streampipes.processors.filters.jvm.processor.compose.ComposeController;
import org.streampipes.processors.filters.jvm.processor.numericalfilter.NumericalFilterController;
import org.streampipes.processors.filters.jvm.processor.projection.ProjectionController;
import org.streampipes.processors.filters.jvm.processor.textfilter.TextFilterController;
import org.streampipes.processors.filters.jvm.processor.threshold.ThresholdDetectionController;
import org.streampipes.processors.imageprocessing.jvm.processor.genericclassification.GenericImageClassificationController;
import org.streampipes.processors.imageprocessing.jvm.processor.imagecropper.ImageCropperController;
import org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.ImageEnrichmentController;
import org.streampipes.processors.imageprocessing.jvm.processor.qrreader.QrCodeReaderController;
import org.streampipes.processors.siddhi.stop.StreamStopController;
import org.streampipes.processors.siddhi.trend.TrendController;
import org.streampipes.processors.transformation.jvm.processor.array.count.CountArrayController;
import org.streampipes.processors.transformation.jvm.processor.array.split.SplitArrayController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.counter.BooleanCounterController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.inverter.BooleanInverterController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping.BooleanTimekeepingController;
import org.streampipes.processors.transformation.jvm.processor.booloperator.timer.BooleanTimerController;
import org.streampipes.processors.transformation.jvm.processor.csvmetadata.CsvMetadataEnrichmentController;
import org.streampipes.processors.transformation.jvm.processor.task.TaskDurationController;
import org.streampipes.processors.transformation.jvm.processor.timestampextractor.TimestampExtractorController;
import org.streampipes.processors.transformation.jvm.processor.value.change.ChangedValueDetectionController;
import org.streampipes.processors.transformation.jvm.processor.value.duration.CalculateDurationController;
import org.streampipes.sinks.brokers.jvm.bufferrest.BufferRestController;
import org.streampipes.sinks.brokers.jvm.jms.JmsController;
import org.streampipes.sinks.brokers.jvm.kafka.KafkaController;
import org.streampipes.sinks.brokers.jvm.pulsar.PulsarController;
import org.streampipes.sinks.brokers.jvm.rabbitmq.RabbitMqController;
import org.streampipes.sinks.brokers.jvm.rest.RestController;
import org.streampipes.sinks.databases.jvm.couchdb.CouchDbController;
import org.streampipes.sinks.databases.jvm.influxdb.InfluxDbController;
import org.streampipes.sinks.databases.jvm.iotdb.IotDbController;
import org.streampipes.sinks.databases.jvm.opcua.UpcUaController;
import org.streampipes.sinks.databases.jvm.postgresql.PostgreSqlController;
import org.streampipes.sinks.internal.jvm.dashboard.DashboardController;
import org.streampipes.sinks.internal.jvm.datalake.DataLakeController;
import org.streampipes.sinks.internal.jvm.notification.NotificationController;
import org.streampipes.sinks.notifications.jvm.email.EmailController;
import org.streampipes.sinks.notifications.jvm.onesignal.OneSignalController;
import org.streampipes.sinks.notifications.jvm.slack.SlackNotificationController;

public class AllPipelineElementsInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton
            .getInstance()
            .add(new NumericalFilterController())
            .add(new ThresholdDetectionController())
            .add(new TextFilterController())
            .add(new ProjectionController())
            .add(new ComposeController())
            .add(new TrendController())
            .add(new StreamStopController())
            .add(new CountArrayController())
            .add(new SplitArrayController())
            .add(new CalculateDurationController())
            .add(new ChangedValueDetectionController())
            .add(new TimestampExtractorController())
            .add(new BooleanCounterController())
            .add(new BooleanInverterController())
            .add(new BooleanTimekeepingController())
            .add(new BooleanTimerController())
            .add(new CsvMetadataEnrichmentController())
            .add(new TaskDurationController())
            .add(new ImageEnrichmentController())
            .add(new ImageCropperController())
            .add(new QrCodeReaderController())
            .add(new GenericImageClassificationController())
            .add(new KafkaController())
            .add(new JmsController())
            .add(new RestController())
            .add(new BufferRestController())
            .add(new RabbitMqController())
            .add(new PulsarController())
            .add(new CouchDbController())
            .add(new InfluxDbController())
            .add(new UpcUaController())
            .add(new PostgreSqlController())
            .add(new IotDbController())
            .add(new NotificationController())
            .add(new DataLakeController())
            .add(new DashboardController())
            .add(new EmailController())
            .add(new OneSignalController())
            .add(new SlackNotificationController());


    DeclarersSingleton.getInstance().registerDataFormats(new JsonDataFormatFactory(),
            new CborDataFormatFactory(),
            new SmileDataFormatFactory(),
            new FstDataFormatFactory());

    DeclarersSingleton.getInstance().registerProtocols(new SpKafkaProtocolFactory(),
            new SpJmsProtocolFactory());

    new AllPipelineElementsInit().init(AllPipelineElementsConfig.INSTANCE);
  }
}
