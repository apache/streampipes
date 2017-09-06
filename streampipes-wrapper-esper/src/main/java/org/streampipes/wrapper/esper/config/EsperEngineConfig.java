package org.streampipes.wrapper.esper.config;

import org.streampipes.wrapper.esper.writer.SEPAWriter;
import org.streampipes.wrapper.esper.writer.Writer;
import org.streampipes.wrapper.routing.SpOutputCollector;

public class EsperEngineConfig {


    public static <T> Writer getDefaultWriter(SpOutputCollector collector, T params)
    {
        return new SEPAWriter(collector);
    }
}
