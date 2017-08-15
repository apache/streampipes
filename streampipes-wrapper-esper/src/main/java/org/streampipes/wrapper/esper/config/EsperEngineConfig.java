package org.streampipes.wrapper.esper.config;

import org.streampipes.wrapper.OutputCollector;
import org.streampipes.wrapper.esper.writer.SEPAWriter;
import org.streampipes.wrapper.esper.writer.Writer;

public class EsperEngineConfig {


    public static <T> Writer getDefaultWriter(OutputCollector collector, T params)
    {
        return new SEPAWriter(collector);
    }
}
