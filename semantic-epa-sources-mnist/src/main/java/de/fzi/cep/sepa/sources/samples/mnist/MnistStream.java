package de.fzi.cep.sepa.sources.samples.mnist;

import ch.qos.logback.core.net.SyslogOutputStream;
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

import java.io.File;

public class MnistStream implements EventStreamDeclarer {

    private static String topic = "de.fzi.cep.mnist";
    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();
    private static String dataFolder = System.getProperty("user.home") + File.separator +"Coding" +File.separator +"data" +File.separator +"mnist" +File.separator;


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
        return true;
    }
}
