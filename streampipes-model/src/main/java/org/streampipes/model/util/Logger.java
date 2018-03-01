package org.streampipes.model.util;

import org.slf4j.LoggerFactory;

public class Logger {

    private org.slf4j.Logger LOG;

    private String correspondingPipeline;

    private String peUri;

    public static Logger getLogger(Class clazz, String correspondingPipeline, String peUri) {
        return new Logger(clazz, correspondingPipeline, peUri);
    }

    private Logger(Class clazz, String correspondingPipeline, String peUri){
        this.correspondingPipeline = correspondingPipeline;
        this.peUri = peUri;

        LOG = LoggerFactory.getLogger(clazz);
    }

    public void info(String s) {
        LOG.info("correspondingPipeline: " + this.correspondingPipeline + " - "
                + "URI: " + this.peUri + " - "
                + s);
    }

    public void trace(String s) {
        LOG.trace("correspondingPipeline: " + this.correspondingPipeline + " - "
                + "URI: " + this.peUri + " - "
                + s);
    }

    public void debug(String s) {
        LOG.debug("correspondingPipeline: " + this.correspondingPipeline + " - "
                + "URI: " + this.peUri + " - "
                + s);
    }

    public void error(String s) {
        LOG.error("correspondingPipeline: " + this.correspondingPipeline + " - "
                + "URI: " + this.peUri + " - "
                + s);
    }

    public void warn(String s) {
        LOG.warn("correspondingPipeline: " + this.correspondingPipeline + " - "
                + "URI: " + this.peUri + " - "
                + s);
    }

}
