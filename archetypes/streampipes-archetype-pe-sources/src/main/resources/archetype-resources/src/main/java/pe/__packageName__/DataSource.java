
package ${package}.pe.${elementName};

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataSourceBuilder;

import java.util.Arrays;
import java.util.List;


public class ${classNamePrefix}Source implements SemanticEventProducerDeclarer {

  public DataSourceDescription declareModel() {
    return DataSourceBuilder.create("${package}.${packageName}.source", "${classNamePrefix} " +
        "Source", "")
            .build();
  }

  public List<DataStreamDeclarer> getEventStreams() {
    return Arrays.asList(new ${classNamePrefix}Stream());
  }
}