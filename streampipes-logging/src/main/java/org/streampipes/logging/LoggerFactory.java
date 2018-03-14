package org.streampipes.logging;

import org.streampipes.container.model.PeConfig;
import org.streampipes.logging.api.Logger;
import org.streampipes.logging.impl.PeLogger;

public class LoggerFactory {

    public static Logger getPeLogger(Class clazz, String correspondingPipeline, String peUri, PeConfig peConfig) {
        return new PeLogger(clazz, correspondingPipeline, peUri, peConfig);
    }
}
