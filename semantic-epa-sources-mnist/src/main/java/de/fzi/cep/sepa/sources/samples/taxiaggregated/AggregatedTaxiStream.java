package de.fzi.cep.sepa.sources.samples.taxiaggregated;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.Geo;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sdk.builder.DataStreamBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Formats;
import de.fzi.cep.sepa.sdk.helpers.Protocols;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReaderSettings;
import de.fzi.cep.sepa.sources.samples.adapter.csv.CsvReplayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public class AggregatedTaxiStream implements EventStreamDeclarer {
    static final Logger LOG = LoggerFactory.getLogger(AggregatedTaxiStream.class);

    private static String kafkaHost = ClientConfiguration.INSTANCE.getKafkaHost();
    private static int kafkaPort = ClientConfiguration.INSTANCE.getKafkaPort();

    private String topic = "de.fzi.cep.sep.taxi";
    private String dataFolder;

    private boolean isExecutable = false;
    private String name = "taxi";

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

                .property(EpProperties.stringEp("delay_label", SO.Text))
                .property(EpProperties.integerEp(CountAggregateConstants.AGGREGATE_TAXI_COUNT, SO.Number))
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
