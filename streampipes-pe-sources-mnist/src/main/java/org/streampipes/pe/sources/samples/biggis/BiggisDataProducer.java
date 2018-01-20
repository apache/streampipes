package org.streampipes.pe.sources.samples.biggis;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.pe.sources.samples.config.MlSourceConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BiggisDataProducer implements SemanticEventProducerDeclarer {

    static final Logger LOG = LoggerFactory.getLogger(BiggisDataProducer.class);

    public static String dataFolder = MlSourceConfig.INSTANCE.getDataLocation() + "biggis/";

    @Override
    public DataSourceDescription declareModel() {
        return new DataSourceDescription("source_biggis_data", "Big gis data producer", "Produces data to test " +
                "the machine learning library");
    }

    @Override
    public List<DataStreamDeclarer> getEventStreams() {

        List<DataStreamDeclarer> streams = new ArrayList<DataStreamDeclarer>();
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
