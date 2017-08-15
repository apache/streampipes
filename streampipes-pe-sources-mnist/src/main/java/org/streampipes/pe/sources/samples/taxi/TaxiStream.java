package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.Geo;
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


public class TaxiStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(TaxiStream.class);

    private static String kafkaHost = MlSourceConfig.INSTANCE.getKafkaHost();
    private static int kafkaPort = MlSourceConfig.INSTANCE.getKafkaPort();

    private String topic = "de.fzi.cep.sep.taxi";
    private String dataFolder;

    private boolean isExecutable = false;
    private String name = "taxi";

    public TaxiStream() {
        topic += ".stream";
    }

    public TaxiStream(String rootFolder, String folderName) {
        topic += "." + folderName;
        name = folderName;
        dataFolder = rootFolder + folderName + File.separator;
        isExecutable = true;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {

        String baseDomainUrl = "http://de.fzi.cep.";

        EventStream stream = DataStreamBuilder
                .create(name, name, "Produces a replay of the mnist dataset")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
//                .format(SupportedFormats.jsonFormat())
//                .protocol(SupportedProtocols.kafka())
//                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))
                // TODO add schema here
                .property(EpProperties.doubleEp("vendor_id", baseDomainUrl + "vendor_id"))
                .property(EpProperties.longEp("tpep_pickup_datetime", baseDomainUrl + "tpep_pickup_datetime"))
                .property(EpProperties.longEp("tpep_dropoff_datetime", baseDomainUrl + "tpep_dropoff_datetime"))
                .property(EpProperties.doubleEp("passenger_count", baseDomainUrl + "passenger_count"))
                .property(EpProperties.doubleEp("trip_distance", baseDomainUrl + "trip_distance"))
                .property(EpProperties.longEp("pickup_longitude", Geo.lng))
                .property(EpProperties.longEp("pickup_latitude", Geo.lat))
                .property(EpProperties.doubleEp("ratecode_id", baseDomainUrl + "ratecode_id"))
                .property(EpProperties.longEp("dropoff_longitude", Geo.lng))
                .property(EpProperties.longEp("dropoff_latitude", Geo.lat))
                .property(EpProperties.doubleEp("payment_type", baseDomainUrl + "payment_type"))
                .property(EpProperties.doubleEp("fare_amount", baseDomainUrl + "fare_amount"))
                .property(EpProperties.doubleEp("extra", baseDomainUrl + "extra"))
                .property(EpProperties.doubleEp("mta_tax", baseDomainUrl + "mta_tax"))
                .property(EpProperties.doubleEp("tip_amount", baseDomainUrl + "tip_amount"))
                .property(EpProperties.doubleEp("tolls_amount", baseDomainUrl + "tolls_amount"))
                .property(EpProperties.doubleEp("improvement_surcharge", baseDomainUrl + "improvement_surcharge"))
                .property(EpProperties.doubleEp("total_amount", baseDomainUrl + "total_amount"))
                .property(EpProperties.doubleEp("read_time", baseDomainUrl + "read_time"))
                .build();


        return stream;
    }

    @Override
    public void executeStream() {

        if (isExecutable) {

            File[] allFiles = new File(dataFolder).listFiles();
            if (allFiles != null && allFiles.length > 0) {


                CsvReaderSettings csvReaderSettings = new CsvReaderSettings(Arrays.asList(allFiles), ",", 0, false);

                EventProducer producer = new SpKafkaProducer(MlSourceConfig.INSTANCE.getKafkaUrl(), topic);

                //TODO change Simulation Settings
                CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new TaxiLineTransformer());

                Thread thread = new Thread(csvReplayTask);
                thread.start();

            } else {
                LOG.error("The Folder: " + dataFolder + " is empty");
            }
        } else {
            LOG.error("The SEP TaxiStream is not executable");
        }
    }

    @Override
    public boolean isExecutable() {
        return isExecutable;
    }
}
