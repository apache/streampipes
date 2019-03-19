#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.main;

import ${groupId}.container.init.DeclarersSingleton;
import ${groupId}.container.standalone.init.StandaloneModelSubmitter;
import ${package}.config.Config;
import ${package}.pe.${packageName}.DataSource;

public class Init extends StandaloneModelSubmitter {

  public static void main(String[] args) throws Exception {
    DeclarersSingleton.getInstance()
            .add(new DataSource());

    new Init().init(ActionConfig.INSTANCE);

  }
}
