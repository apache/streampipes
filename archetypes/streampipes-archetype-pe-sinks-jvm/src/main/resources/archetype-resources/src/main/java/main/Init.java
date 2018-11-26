#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;

import ${package}.config.Config;
import ${package}.pe.sink.${packageName}.${classNamePrefix}Controller;

public class Init extends StandaloneModelSubmitter {

  public static void main(String[] args) throws Exception {
    DeclarersSingleton.getInstance()
            .add(new ${classNamePrefix}Controller());

    DeclarersSingleton.getInstance().setPort(Config.INSTANCE.getPort());
    DeclarersSingleton.getInstance().setHostName(Config.INSTANCE.getHost());

    DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());

    new Init().init(Config.INSTANCE);

  }


}
