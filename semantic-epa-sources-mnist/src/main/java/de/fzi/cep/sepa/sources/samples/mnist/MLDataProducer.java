package de.fzi.cep.sepa.sources.samples.mnist;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MLDataProducer implements SemanticEventProducerDeclarer {

    static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

//    public static String dataFolder =System.getProperty("user.home") + File.separator +".streampipes" +
//            File.separator +"sources" + File.separator +"data" + File.separator +"mnist" + File.separator;

    public static String dataFolder = ClientConfiguration.INSTANCE.getMnistDatalocation();


//    private static String dataFolder = System.getProperty("user.home") + File.separator +"Coding" +File.separator +
//            "data" +File.separator +"semmnist" + File.separator;

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
