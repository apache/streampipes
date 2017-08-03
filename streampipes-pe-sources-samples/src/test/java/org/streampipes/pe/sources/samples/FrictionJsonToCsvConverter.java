package org.streampipes.pe.sources.samples;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.Gson;
import org.streampipes.commons.config.old.ConfigurationManager;
import org.streampipes.pe.sources.samples.friction.FrictionRawEvent;
import org.streampipes.pe.sources.samples.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

/**
 * Created by riemer on 10.11.2016.
 */
public class FrictionJsonToCsvConverter {

    private static final String FrictionReplayFilename = "friction-coeff.txt";
    private static final String FrictionDirectory = "data/";

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();

            Optional<BufferedReader> readerOpt = Utils.getReader(buildFile());
            CSVWriter writer = new CSVWriter(new FileWriter(buildOutputFile()), ',');

            if (readerOpt.isPresent())

            {

                BufferedReader br = readerOpt.get();
                String line;

                while ((line = br.readLine()) != null) {
                    FrictionRawEvent rawEvent = gson.fromJson(line, FrictionRawEvent.class);
                    String[] entries = makeEntries(rawEvent);
                    writer.writeNext(entries);

                }
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] makeEntries(FrictionRawEvent rawEvent) {
        String[] entries = new String[6];
        entries[0] = rawEvent.getStart();
        entries[1] = rawEvent.getEnd();
        entries[2] = String.valueOf(rawEvent.getGearbox().getValue());
        entries[3] = String.valueOf(rawEvent.getGearbox().getMeanTemperature());
        entries[4] = String.valueOf(rawEvent.getSwivel().getValue());
        entries[5] = String.valueOf(rawEvent.getSwivel().getMeanTemperature());
        return entries;
    }

    private static File buildFile() {
        File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() + "/" + FrictionDirectory + FrictionReplayFilename);
        return file;
    }

    private static File buildOutputFile() {
        File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() + "/" + FrictionDirectory + "output.csv");
        return file;
    }
}
