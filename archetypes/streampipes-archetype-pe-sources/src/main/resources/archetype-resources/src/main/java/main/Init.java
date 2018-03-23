#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.main;

import ${groupId}.container.init.DeclarersSingleton;
import ${groupId}.container.standalone.init.StandaloneModelSubmitter;
import ${groupId}.dataformat.json.JsonDataFormatFactory;
import ${groupId}.messaging.kafka.SpKafkaProtocolFactory;
import ${package}.config.ActionConfig;
import ${package}.pe.${elementName}.${__elementName__}Controller;
public class ActionsInit extends StandaloneModelSubmitter {

  public static void main(String[] args) throws Exception {
    DeclarersSingleton.getInstance()

            .add(new JmsController())

    DeclarersSingleton.getInstance().setPort(ActionConfig.INSTANCE.getPort());
    DeclarersSingleton.getInstance().setHostName(ActionConfig.INSTANCE.getHost());
    DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());

    new ActionsInit().init();

  }


}
