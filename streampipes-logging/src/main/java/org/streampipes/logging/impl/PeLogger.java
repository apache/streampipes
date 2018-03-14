package org.streampipes.logging.impl;

import org.streampipes.container.model.PeConfig;
import org.streampipes.logging.api.Logger;
import org.slf4j.LoggerFactory;

public class PeLogger implements Logger {

    private org.slf4j.Logger LOG;

    private String prefix;



    public PeLogger(Class clazz, String correspondingPipeline, String peUri, PeConfig peConfig){
        this.prefix =  "USERLOG" + " - "
                       + "serverName: " + peConfig.getName()
                       + "correspondingPipeline: " + correspondingPipeline + " - "
                       + "peURI: " + peUri + " - ";

        LOG = LoggerFactory.getLogger(clazz);
    }

    public void info(String s) {
        LOG.info(prefix + s);
    }

    public void trace(String s) {
        LOG.trace(prefix + s);
    }

    public void debug(String s) {
        LOG.debug(prefix + s);
    }

    public void error(String s) {
        LOG.error(prefix + s);
    }

    public void warn(String s) {
        LOG.warn(prefix + s);
    }

}
