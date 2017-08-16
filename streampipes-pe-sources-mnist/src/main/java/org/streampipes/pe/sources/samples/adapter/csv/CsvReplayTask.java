package org.streampipes.pe.sources.samples.adapter.csv;

import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.pe.sources.samples.adapter.AbstractReplayTask;
import org.streampipes.pe.sources.samples.adapter.AdapterSchemaTransformer;
import org.streampipes.pe.sources.samples.adapter.SimulationSettings;

import java.io.File;
import java.util.logging.Logger;

public class CsvReplayTask extends AbstractReplayTask {

    private static final Logger LOG = Logger.getAnonymousLogger();

    private CsvReaderSettings csvSettings;
    private SpKafkaProducer producer;
    private AdapterSchemaTransformer schemaTransformer;



    public CsvReplayTask(CsvReaderSettings csvSettings, SimulationSettings simulationSettings, SpKafkaProducer
            producer, AdapterSchemaTransformer schemaTransformer) {
        super(simulationSettings);
        this.csvSettings = csvSettings;
        this.producer = producer;
        this.schemaTransformer = schemaTransformer;
    }

    @Override
    public void run() {

        for (File file : csvSettings.getCsvInputFiles()) {
            new CsvReader(file, csvSettings, simulationSettings, producer, schemaTransformer).read();
        }

        producer.disconnect();
    }
}
