#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;

import ${package}.config.Config;
import ${package}.pe.processor.${packageName}.${classNamePrefix}Controller;

public class Init extends StandaloneModelSubmitter {

  public static void main(String[] args) throws Exception {
    DeclarersSingleton.getInstance()
            .add(new ${classNamePrefix}Controller());

    new Init().init(Config.INSTANCE);

  }


}
