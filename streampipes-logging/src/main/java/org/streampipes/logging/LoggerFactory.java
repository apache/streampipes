package org.streampipes.logging;

import org.streampipes.logging.api.Logger;
import org.streampipes.logging.impl.PeLogger;

public class LoggerFactory {

  //  public static Logger getPeLogger(Class clazz, String correspondingPipeline, String peUri, PeConfig peConfig) {
    public static Logger getPeLogger(Class clazz, String correspondingPipeline, String peUri) {
    //    return new PeLogger(clazz, correspondingPipeline, peUri, peConfig);
        return new PeLogger(clazz, correspondingPipeline, peUri);
    }
}
