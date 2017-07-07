package org.streampipes.pe.sources.samples.mnist;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Groundings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReaderSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReplayTask;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;


public class MnistStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(MnistStream.class);


    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();

    private String topic = "de.fzi.cep.sep.mnist";
    private String dataFolder;

    private boolean isExecutable = false;
    private String name = "mnist";


    public MnistStream() {
        topic += ".stream";
    }

    public MnistStream(String rootFolder, String folderName) {
        topic += "." + folderName;
        name = folderName;
        dataFolder = rootFolder + folderName + File.separator;
        isExecutable = true;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {

        //TODO extend the Builder Pattern for List Properites in the SDK
        EventProperty ep1 = EpProperties.doubleEp("pixel", "http://de.fzi.cep.blackwhite");
        EventProperty image = new EventPropertyList("image", ep1);

        EventStream stream = DataStreamBuilder
                .create(name, name, "Produces a replay of the mnist dataset")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
                .property(EpProperties.integerEp("label", "http://de.fzi.cep.label"))
                .property(image)
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

                CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new MnistLineTransformer());

                Thread thread = new Thread(csvReplayTask);
                thread.start();

            } else {
                LOG.error("The Folder: " + dataFolder + " is empty");
            }
        } else {
            LOG.error("The SEP MnistStream is not executable");
        }
    }


    @Override
    public boolean isExecutable() {
        return isExecutable;
    }
}
