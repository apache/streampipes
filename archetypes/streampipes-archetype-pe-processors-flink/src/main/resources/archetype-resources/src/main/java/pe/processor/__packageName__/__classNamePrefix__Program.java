#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.pe.processor.${packageName};

import ${package}.config.Config;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.io.Serializable;

public class ${classNamePrefix}Program extends
        FlinkDataProcessorRuntime<${classNamePrefix}Parameters>
implements Serializable {

  private static final long serialVersionUID = 1L;

  public ${classNamePrefix}Program(${classNamePrefix}Parameters params, boolean debug) {
    super(params, debug);
  }

  @Override
  protected FlinkDeploymentConfig getDeploymentConfig() {
    return new FlinkDeploymentConfig(Config.JAR_FILE,
            Config.INSTANCE.getFlinkHost(), Config.INSTANCE.getFlinkPort());
  }

  @Override
  protected DataStream<Event> getApplicationLogic(
        DataStream<Event>... messageStream) {

    return messageStream[0]
        .flatMap(new ${classNamePrefix}());
  }
}
