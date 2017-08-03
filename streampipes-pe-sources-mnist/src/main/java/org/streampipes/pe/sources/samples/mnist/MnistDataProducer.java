package org.streampipes.pe.sources.samples.mnist;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.model.impl.graph.SepDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MnistDataProducer implements SemanticEventProducerDeclarer {

    static final Logger LOG = LoggerFactory.getLogger(MnistDataProducer.class);

    public static String dataFolder = ClientConfiguration.INSTANCE.getDatalocation() + "mnist/";

    @Override
    public SepDescription declareModel() {
        return new SepDescription("source_ml_data", "ML data producer", "Produces data to test " +
                "the machine learning library");
    }

    @Override
    public List<EventStreamDeclarer> getEventStreams() {

        List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
        streams.add(new MnistStream());

        File[] allFiles = new File(dataFolder).listFiles();
        if (allFiles != null) {
            for (File f : allFiles) {
                if (f.isDirectory()) {
                    streams.add(new MnistStream(dataFolder, f.getName()));
                    LOG.info("Started replay of MNIST data from folder " + dataFolder + f.getName());
                }
            }
        } else {
            LOG.info("There is no replay data for a MNIST stream. Route of the data folder: " + dataFolder);
        }


        return streams;
    }
}
