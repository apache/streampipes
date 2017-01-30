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
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReaderSettings;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReplayTask;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MnistStream implements EventStreamDeclarer {

    private static String topic = "de.fzi.cep.sep.mnist";
    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();
    private static String dataFolder = System.getProperty("user.home") + File.separator +"Coding" +File.separator +
            "data" +File.separator +"semmnist" +File.separator + "mnist_10"+File.separator + "mnist_10.csv";

    private boolean isExecutable = false;

    public MnistStream() {
        topic += ".stream";
    }

    public MnistStream(String folderName) {
        topic += folderName;
        isExecutable = true;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {

        //TODO extend the Builder Pattern for List Properites in the SDK
        EventProperty ep1 = EpProperties.doubleEp("pixel", "http://de.fzi.cep.blackwhite");
        EventProperty image = new EventPropertyList("image", ep1);

        EventStream stream = DataStreamBuilder
                .create("mnist", "mnist", "Produces a replay of the mnist dataset")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
                .property(EpProperties.integerEp("label", "http://de.fzi.cep.label"))
                .property(image)
                .build();


        return stream;
    }

    @Override
    public void executeStream() {

        // TODO
        List<File> allFiles = new ArrayList<>();
        allFiles.add(new File(dataFolder));

        CsvReaderSettings csvReaderSettings = new CsvReaderSettings(allFiles, ",", 0, false);

        EventProducer producer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);

        CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new MnistLineTransformer());

        csvReplayTask.run();


//        System.out.println("Execute Mnist");
//        EventProducer publisher = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topic);
//
//        // TODO implement Line Parser
//        LineParser mnistLineParser = new MnistParser();
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
