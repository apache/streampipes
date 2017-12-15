package org.streampipes.pe.sinks.standalone.samples.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.alarm.AlarmLightController;
import org.streampipes.pe.sinks.standalone.samples.couchdb.CouchDbController;
import org.streampipes.pe.sinks.standalone.samples.dashboard.DashboardController;
import org.streampipes.pe.sinks.standalone.samples.email.EmailController;
import org.streampipes.pe.sinks.standalone.samples.file.FileController;
import org.streampipes.pe.sinks.standalone.samples.jms.JmsController;
import org.streampipes.pe.sinks.standalone.samples.kafka.KafkaController;
import org.streampipes.pe.sinks.standalone.samples.notification.NotificationController;
import org.streampipes.pe.sinks.standalone.samples.rabbitmq.RabbitMqController;
public class ActionsInit extends StandaloneModelSubmitter {

  public static void main(String[] args) throws Exception {
    DeclarersSingleton.getInstance()

            .add(new JmsController())
            .add(new FileController())
            .add(new NotificationController())
            .add(new KafkaController())
            .add(new CouchDbController())
            .add(new DashboardController())
            .add(new AlarmLightController())
            .add(new RabbitMqController())
            .add(new EmailController());

    DeclarersSingleton.getInstance().setPort(ActionConfig.INSTANCE.getPort());
    DeclarersSingleton.getInstance().setHostName(ActionConfig.INSTANCE.getHost());
    DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());

    new ActionsInit().init(ActionConfig.INSTANCE);

  }


}
