package org.streampipes.pe.sources.samples.taxiaggregated;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.config.old.ClientConfiguration;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Formats;
import org.streampipes.sdk.helpers.Protocols;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReaderSettings;
import org.streampipes.pe.sources.samples.adapter.csv.CsvReplayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class AggregatedTaxiStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(AggregatedTaxiStream.class);

    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();

    private String topic = "de.fzi.cep.sep.aggregatedtaxi";
    private String dataFolder;

    private boolean isExecutable = false;
    private String name = "aggregatedtaxi";

    public AggregatedTaxiStream() {
        topic += ".stream";
    }

    public AggregatedTaxiStream(String rootFolder, String folderName) {
        topic += "." + folderName;
        name = folderName;
        dataFolder = rootFolder + folderName + File.separator;
        isExecutable = true;
    }

    @Override
    public EventStream declareModel(SepDescription sep) {

        EventStream stream = DataStreamBuilder
                .create(name, name, "Produces a replay of the mnist dataset")
//                .format(Groundings.jsonFormat())
//                .protocol(Groundings.kafkaGrounding(kafkaHost, kafkaPort, topic))

                .property(EpProperties.longEp(CountAggregateConstants.WINDOW_TIME_START, SO.DateTime))
                .property(EpProperties.longEp(CountAggregateConstants.WINDOW_TIME_END, SO.DateTime))
                .property(EpProperties.integerEp(CountAggregateConstants.PASSENGER_COUNT_AVG, SO.Number))
                .property(EpProperties.doubleEp(CountAggregateConstants.TRIP_DISTANCE_AVG, SO.Number))
                .property(EpProperties.doubleEp(CountAggregateConstants.EXTRA_AVG, SO.Number))
                .property(EpProperties.doubleEp(CountAggregateConstants.TIP_AMOUNT_AVG, SO.Number))
                .property(EpProperties.doubleEp(CountAggregateConstants.TOLLS_AMOUNT_AVG, SO.Number))
                .property(EpProperties.doubleEp(CountAggregateConstants.FARE_AMOUNT_AVG, SO.Number))
                .property(EpProperties.doubleEp(CountAggregateConstants.TOTAL_AMOUNT_AVG, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_1, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_2, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_3, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_4, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_5, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.RATE_CODE_ID_6, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_1, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_2, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_3, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_4, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_5, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.PAYMENT_TYPE_6, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.MTA_TAX, SO.Number))
                .property(EpProperties.integerEp(CountAggregateConstants.IMPROVEMENT_SURCHARGE, SO.Number))

                .property(EpProperties.doubleEp(CountAggregateConstants.GRID_LAT_NW_KEY, Geo.lat))
                .property(EpProperties.doubleEp(CountAggregateConstants.GRID_LON_NW_KEY, Geo.lng))
                .property(EpProperties.doubleEp(CountAggregateConstants.GRID_LAT_SE_KEY, Geo.lat))
                .property(EpProperties.doubleEp(CountAggregateConstants.GRID_LON_SE_KEY, Geo.lng))
                .property(EpProperties.stringEp(CountAggregateConstants.GRID_CELL_ID, SO.Text))
                .format(Formats.jsonFormat())
                .protocol(Protocols.kafka(ClientConfiguration.INSTANCE.getKafkaHost(),
                        ClientConfiguration.INSTANCE.getKafkaPort(), topic))
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

                //TODO change Simulation Settings
                CsvReplayTask csvReplayTask = new CsvReplayTask(csvReaderSettings, SimulationSettings.PERFORMANCE_TEST, producer, new AggregatedTaxiLineTransformer());

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
