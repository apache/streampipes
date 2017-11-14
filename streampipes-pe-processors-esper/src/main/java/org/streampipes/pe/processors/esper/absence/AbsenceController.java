package org.streampipes.pe.processors.esper.absence;

import org.streampipes.commons.Utils;
import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.CustomOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.wrapper.ConfiguredEventProcessor;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbsenceController extends StandaloneEventProcessorDeclarerSingleton<AbsenceParameters> {

  @Override
  public DataProcessorDescription declareModel() {

    SpDataStream stream1 = new SpDataStream();
    SpDataStream stream2 = new SpDataStream();

    DataProcessorDescription desc = new DataProcessorDescription("absence", "Absence", "Detects whether an event does not arrive within a specified time after the occurrence of another event.");
    desc.setCategory(Arrays.asList(DataProcessorType.PATTERN_DETECT.name()));

    stream1.setUri(EsperConfig.serverUrl + "/" + Utils.getRandomString());
    stream2.setUri(EsperConfig.serverUrl + "/" + Utils.getRandomString());
    desc.addEventStream(stream1);
    desc.addEventStream(stream2);

    List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
    strategies.add(new CustomOutputStrategy(false));
    desc.setOutputStrategies(strategies);

    List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();

    staticProperties.add(StaticProperties.integerFreeTextProperty("timeWindow", "Time Window Size", "Time window size (seconds)"));
    desc.setStaticProperties(staticProperties);
    desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
    return desc;
  }

  @Override
  public ConfiguredEventProcessor<AbsenceParameters, EventProcessor<AbsenceParameters>> onInvocation(DataProcessorInvocation sepa) {

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    int timeWindowSize = Integer.parseInt(
            ((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(sepa, "timeWindow"))).getValue());

    AbsenceParameters staticParam = new AbsenceParameters(sepa, selectProperties, timeWindowSize);

    return new ConfiguredEventProcessor<>(staticParam, Absence::new);
  }
}
