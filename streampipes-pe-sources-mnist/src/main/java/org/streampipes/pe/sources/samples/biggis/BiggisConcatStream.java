package org.streampipes.pe.sources.samples.biggis;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.samples.config.MlSourceConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Groundings;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReaderSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReplayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;


public class BiggisConcatStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(BiggisConcatStream.class);


    private static String kafkaHost = MlSourceConfig.INSTANCE.getKafkaHost();
    private static int kafkaPort = MlSourceConfig.INSTANCE.getKafkaPort();

    private String topic = "de.fzi.cep.sep.biggisconcat";
    private String dataFolder;

    private boolean isExecutable = false;
    private String name = "biggisconcat";


    public BiggisConcatStream() {
        topic += ".stream";
    }

    public BiggisConcatStream(String rootFolder, String folderName) {
        topic += "." + folderName;
        name = folderName;
        dataFolder = rootFolder + folderName + File.separator;
        isExecutable = true;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {

        EventStream stream = DataStreamBuilder
                .create(name, name, "Produces a replay of the biggisconcat dataset")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
                .property(EpProperties.doubleEp("label", "http://de.fzi.cep.label"))
                .property(EpProperties.doubleEp("red0", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp("green0", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp("blue0", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp("nir0", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp("blue1", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp("green1", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp("red1", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp("nir1", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp("blue2", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp("green2", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp("red2", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp("nir2", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp("blue3", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp("green3", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp("red3", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp("nir3", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp("blue4", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp("green4", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp("red4", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp("nir4", "http://de.fzi.cep/nir"))


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

                EventProducer producer = new StreamPipesKafkaProducer(MlSourceConfig.INSTANCE.getKafkaUrl(), topic);

                CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new BiggisConcatLineTransformer());

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
