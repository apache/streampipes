package org.streampipes.wrapper.esper.config;

import org.streampipes.wrapper.esper.writer.SEPAWriter;
import org.streampipes.wrapper.esper.writer.Writer;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;

public class EsperEngineConfig {


    public static <T> Writer getDefaultWriter(EventProcessorOutputCollector collector, T params)
    {
        return new SEPAWriter(collector);
    }
}
