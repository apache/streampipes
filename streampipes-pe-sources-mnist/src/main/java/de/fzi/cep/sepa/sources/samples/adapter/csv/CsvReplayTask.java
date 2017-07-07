package de.fzi.cep.sepa.sources.samples.adapter.csv;

import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.sources.samples.adapter.AbstractReplayTask;
import de.fzi.cep.sepa.sources.samples.adapter.AdapterSchemaTransformer;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;

import java.io.File;
import java.util.logging.Logger;

public class CsvReplayTask extends AbstractReplayTask {

    private static final Logger LOG = Logger.getAnonymousLogger();

    private CsvReaderSettings csvSettings;
    private EventProducer producer;
    private AdapterSchemaTransformer schemaTransformer;



    public CsvReplayTask(CsvReaderSettings csvSettings, SimulationSettings simulationSettings, EventProducer producer, AdapterSchemaTransformer schemaTransformer) {
        super(simulationSettings);
        this.csvSettings = csvSettings;
        this.producer = producer;
        this.schemaTransformer = schemaTransformer;
    }

    @Override
    public void run() {

        producer.openProducer();
        for (File file : csvSettings.getCsvInputFiles()) {
            new CsvReader(file, csvSettings, simulationSettings, producer, schemaTransformer).read();
        }

        producer.closeProducer();
    }
}
