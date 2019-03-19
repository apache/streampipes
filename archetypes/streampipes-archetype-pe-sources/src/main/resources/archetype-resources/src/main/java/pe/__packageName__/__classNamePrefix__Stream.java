#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\' )

package ${package}.pe.${elementName};

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.sources.AbstractAdapterIncludedStream;


public class ${classNamePrefix}Stream extends AbstractAdapterIncludedStream {

  @Override
  public SpDataStream declareModel(DataSourceDescription sep) {
    return DataStreamBuilder.create("${package}-${packageName}", "${classNamePrefix}", "")
            .property(EpProperties.timestampProperty("timestamp"))

            // configure your stream here

            .format(Formats.jsonFormat())
            .protocol(Protocols.kafka(VehicleSimulatorConfig.INSTANCE.getKafkaHost(), VehicleSimulatorConfig.INSTANCE.getKafkaPort(),
                    "TOPIC_SHOULD_BE_CHANGED"))
            .build();
  }

  @Override
  public void executeStream() {

  }
}
