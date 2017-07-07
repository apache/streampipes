package de.fzi.cep.sepa.sources.samples.biggis;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BiggisDataProducer implements SemanticEventProducerDeclarer {

    static final Logger LOG = LoggerFactory.getLogger(BiggisDataProducer.class);

    public static String dataFolder = ClientConfiguration.INSTANCE.getDatalocation() + "biggis/";

    @Override
    public SepDescription declareModel() {
        return new SepDescription("source_biggis_data", "Big gis data producer", "Produces data to test " +
                "the machine learning library");
    }

    @Override
    public List<EventStreamDeclarer> getEventStreams() {

        List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
        streams.add(new BiggisStream());

        File[] allFiles = new File(dataFolder).listFiles();
        if (allFiles != null) {
            for (File f : allFiles) {
                //TODO starts with con_ // check
                if (f.isDirectory()) {
                    if (f.getName().startsWith("con_")){
                        streams.add(new BiggisConcatStream(dataFolder, f.getName()));
                        LOG.info("Started replay of Biggis concat data from folder " + dataFolder + f.getName());
                    } else {
                        streams.add(new BiggisStream(dataFolder, f.getName()));
                        LOG.info("Started replay of Biggis data from folder " + dataFolder + f.getName());
                    }

                }
            }
        } else {
            LOG.info("There is no replay data for a Biggis stream. Route of the data folder: " + dataFolder);
        }


        return streams;
    }
}
