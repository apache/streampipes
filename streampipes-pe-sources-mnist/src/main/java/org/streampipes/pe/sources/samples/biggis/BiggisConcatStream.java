package org.streampipes.pe.sources.samples.biggis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReaderSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReplayTask;
import org.streampipes.pe.sources.samples.config.MlSourceConfig;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Groundings;
import org.streampipes.sdk.helpers.Labels;

import java.io.File;
import java.util.Arrays;


public class BiggisConcatStream implements DataStreamDeclarer {
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
    public SpDataStream declareModel(DataSourceDescription sep) {

        SpDataStream stream = DataStreamBuilder
                .create(name, name, "Produces a replay of the biggisconcat dataset")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
                .property(EpProperties.doubleEp(Labels.empty(), "label", "http://de.fzi.cep.label"))
                .property(EpProperties.doubleEp(Labels.empty(), "red0", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp(Labels.empty(), "green0", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp(Labels.empty(), "blue0", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp(Labels.empty(), "nir0", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp(Labels.empty(), "blue1", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp(Labels.empty(), "green1", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp(Labels.empty(), "red1", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp(Labels.empty(), "nir1", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp(Labels.empty(), "blue2", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp(Labels.empty(), "green2", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp(Labels.empty(), "red2", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp(Labels.empty(), "nir2", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp(Labels.empty(), "blue3", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp(Labels.empty(), "green3", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp(Labels.empty(), "red3", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp(Labels.empty(), "nir3", "http://de.fzi.cep/nir"))

                .property(EpProperties.doubleEp(Labels.empty(), "blue4", "http://dbpedia.org/ontology/rgbCoordinateBlue"))
                .property(EpProperties.doubleEp(Labels.empty(), "green4", "http://dbpedia.org/ontology/rgbCoordinateGreen"))
                .property(EpProperties.doubleEp(Labels.empty(), "red4", "http://dbpedia.org/ontology/rgbCoordinateRed"))
                .property(EpProperties.doubleEp(Labels.empty(), "nir4", "http://de.fzi.cep/nir"))


                .property(EpProperties.doubleEp(Labels.empty(), "spacial_1", "http://de.fzi.cep/spacial_1"))
                .property(EpProperties.doubleEp(Labels.empty(), "spacial_2", "http://de.fzi.cep/spacial_2"))
                .property(EpProperties.doubleEp(Labels.empty(), "x_tile", "http://de.fzi.cep/x_tile"))
                .property(EpProperties.doubleEp(Labels.empty(), "y_tile", "http://de.fzi.cep/y_tile"))
                .build();


        return stream;
    }

    @Override
    public void executeStream() {

        if (isExecutable) {

            File[] allFiles = new File(dataFolder).listFiles();
            if (allFiles != null && allFiles.length > 0) {


                CsvReaderSettings csvReaderSettings = new CsvReaderSettings(Arrays.asList(allFiles), ",", 0, false);

                SpKafkaProducer producer = new SpKafkaProducer(MlSourceConfig.INSTANCE.getKafkaUrl(), topic);

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
