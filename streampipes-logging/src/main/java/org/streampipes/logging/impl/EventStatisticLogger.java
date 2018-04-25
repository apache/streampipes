package org.streampipes.logging.impl;

import org.slf4j.LoggerFactory;

public class EventStatisticLogger {


    private String prefix;

   // public static void log(org.streampipes.model.base.InvocableStreamPipesEntity graph) {
    public static void log(String name, String correspondingPipeline, String source) {
        String prefix =  "SYSTEMLOG - EVENT STATISTIC" + " - "
                // + "serviceName: " + peConfig.getName() + " - "
                + "correspondingPipeline: " + correspondingPipeline + " - "
                + "peURI: " + source + " - ";

        org.slf4j.Logger logger = LoggerFactory.getLogger(name);
        logger.info(prefix + 1);

    }

}
