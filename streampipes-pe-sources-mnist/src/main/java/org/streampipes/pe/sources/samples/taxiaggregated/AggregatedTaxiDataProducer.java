package org.streampipes.pe.sources.samples.taxiaggregated;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.pe.sources.samples.config.MlSourceConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AggregatedTaxiDataProducer implements SemanticEventProducerDeclarer {

    static final Logger LOG = LoggerFactory.getLogger(AggregatedTaxiDataProducer.class);

    public static String dataFolder = MlSourceConfig.INSTANCE.getDataLocation() + "aggregated_taxi/";

    @Override
    public SepDescription declareModel() {
        return new SepDescription("sources_aggregated_taxi_data", "Aggregated Taxi data producer", "Produces data to test " +
                "the machine learning library");
    }


    @Override
    public List<EventStreamDeclarer> getEventStreams() {

        List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();

        streams.add(new AggregatedTaxiStream());

        File[] allFiles = new File(dataFolder).listFiles();
        if (allFiles != null) {
            for (File f : allFiles) {
                if (f.isDirectory()) {
                    //TODO add stream
                    streams.add(new AggregatedTaxiStream(dataFolder, f.getName()));
                    LOG.info("Started replay of aggregated taxi data from folder " + dataFolder + f.getName());
                }
            }
        } else {
            LOG.info("There is no replay data for a aggregated taxi stream. Route of the data folder: " + dataFolder);
        }


        return streams;
    }

}
