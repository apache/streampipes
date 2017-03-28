package de.fzi.cep.sepa.sources.samples.adapter.csv;

import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.sources.samples.adapter.AdapterSchemaTransformer;
import de.fzi.cep.sepa.sources.samples.adapter.JsonTransformer;
import de.fzi.cep.sepa.sources.samples.adapter.SimulationSettings;
import de.fzi.cep.sepa.sources.samples.adapter.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class CsvReader {

    static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

    private File file;
    private CsvReaderSettings csvSettings;
    private SimulationSettings simulationSettings;
    private EventProducer producer;
    private AdapterSchemaTransformer schemaTransformer;

    public CsvReader(File file, CsvReaderSettings csvSettings, SimulationSettings simulationSettings,
                     EventProducer producer, AdapterSchemaTransformer schemaTransformer) {
        this.file = file;
        this.csvSettings = csvSettings;
        this.simulationSettings = simulationSettings;
        this.producer = producer;
        this.schemaTransformer = schemaTransformer;
    }

    public void read() {

        long previousTime = 0;

        Optional<BufferedReader> readerOpt = Utils.getReader(file);
        if (readerOpt.isPresent()) {
            try {
                BufferedReader br = readerOpt.get();
                String line;
                long counter = 0;

                while ((line = br.readLine()) != null) {
                    line = removeWhiteSpace(line);

                    if ((counter == 0) && csvSettings.isHeaderIncluded()) {
                        counter++;
                        continue;
                    }

                    String[] map = line.split(csvSettings.getColumnSeparator());

                    //TODO add wait here when time is needed

                    // Transfrom the data to a Map
                    Map<String, Object> data = schemaTransformer.transform(map);

                    //Serialize the data to JSON
                    JsonTransformer jsonTransformer = new JsonTransformer();

                    producer.publish(jsonTransformer.transform(data));

                    counter ++;
                    if (counter % 1000 == 0) LOG.info(counter + " Events sent.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOG.error("The File: " + file.toString() + " does not exist");
        }

    }

    private String removeWhiteSpace(String s) {
        String result = s;
        result = result.replaceAll("; ", ";");
        result = result.replaceAll(", ", ",");

        return result;
    }
}
