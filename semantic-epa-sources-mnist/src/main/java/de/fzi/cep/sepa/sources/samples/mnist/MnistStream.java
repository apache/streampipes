package de.fzi.cep.sepa.sources.samples.mnist;

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
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReader;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReaderSettings;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReplayTask;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MnistStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);


    private static String topic = "de.fzi.cep.sep.mnist";
    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();

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
            if (allFiles.length > 0) {


                CsvReaderSettings csvReaderSettings = new CsvReaderSettings(Arrays.asList(allFiles), ",", 0, false);

                EventProducer producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);

                CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new MnistLineTransformer());

                csvReplayTask.run();
            } else {
                LOG.error("The Folder: " + dataFolder + " is empty");
            }
        } else {
            LOG.error("The SEP MnistStream is not executable");
        }


//        System.out.println("Execute Mnist");
//        EventProducer publisher = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);
//
//        // TODO implement Line Parser
//       	CsvReadingTask csvReadingTask = new CsvReadingTask(getReadingTasks(), ",", "variable_timestamp", mnistLineParser, true);
//
//		Thread mouldingReplayThread = new Thread(new CsvPublisher(publisher, csvReadingTask, SimulationSettings.DEMONSTRATE_10));
//		mouldingReplayThread.start();

    }

//    private List<FolderReadingTask> getReadingTasks() {
//        List<FolderReadingTask> tasks = new ArrayList<>();
//
//        tasks.add(new FolderReadingTask(dataFolder, "mnist_1", "csv", 0, 1));
//        return tasks;
//    }

    @Override
    public boolean isExecutable() {
        return isExecutable;
    }
}
