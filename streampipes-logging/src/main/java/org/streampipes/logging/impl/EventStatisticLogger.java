package org.streampipes.logging.impl;

import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class EventStatisticLogger implements Serializable {


    private String prefix;

   // public static void log(org.streampipes.model.base.InvocableStreamPipesEntity graph) {
    public static void log(String name, String correspondingPipeline, String source) {
    // TODO: Uncomment when "Event statistic" should be used
        /*    String prefix =  "SYSTEMLOG EVENT STATISTIC" + " - "
                // + "serviceName: " + peConfig.getName() + " - "
                + "correspondingPipeline: " + correspondingPipeline + " - "
                + "peURI: " + source + " - ";

        org.slf4j.Logger logger = LoggerFactory.getLogger(EventStatisticLogger.class);
        logger.info(prefix + 1);
        */
    }

}
