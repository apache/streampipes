package de.fzi.cep.sepa.sources.samples.biggis;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Groundings;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReaderSettings;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReplayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;


public class BiggisStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(BiggisStream.class);


    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();

    private String topic = "de.fzi.cep.sep.biggis";
    private String dataFolder;

    private boolean isExecutable = false;
    private String name = "biggis";


    public BiggisStream() {
        topic += ".stream";
    }

    public BiggisStream(String rootFolder, String folderName) {
        topic += "." + folderName;
        name = folderName;
        dataFolder = rootFolder + folderName + File.separator;
        isExecutable = true;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {

        EventStream stream = DataStreamBuilder
                .create(name, name, "Produces a replay of the biggis dataset")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
                .property(EpProperties.doubleEp("label", "http://de.fzi.cep.label"))
                .property(EpProperties.doubleEp("blue", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp("green", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp("red", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp("nir", "http://de.fzi.cep/nir"))
                .property(EpProperties.doubleEp("spacial_1", "http://de.fzi.cep/spacial_1"))
                .property(EpProperties.doubleEp("spacial_2", "http://de.fzi.cep/spacial_2"))
                .property(EpProperties.doubleEp("x_tile", "http://de.fzi.cep/x_tile"))
                .property(EpProperties.doubleEp("y_tile", "http://de.fzi.cep/y_tile"))
                .build();


        return stream;
    }

    @Override
    public void executeStream() {

        if (isExecutable) {

            File[] allFiles = new File(dataFolder).listFiles();
            if (allFiles != null && allFiles.length > 0) {


                CsvReaderSettings csvReaderSettings = new CsvReaderSettings(Arrays.asList(allFiles), ",", 0, false);

                EventProducer producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);

                CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new BiggisLineTransformer());

                Thread thread = new Thread(csvReplayTask);
                thread.start();

            } else {
                LOG.error("The Folder: " + dataFolder + " is empty");
            }
        } else {
            LOG.error("The SEP BiggisSEP is not executable");
        }
    }


    @Override
    public boolean isExecutable() {
        return isExecutable;
    }
}
