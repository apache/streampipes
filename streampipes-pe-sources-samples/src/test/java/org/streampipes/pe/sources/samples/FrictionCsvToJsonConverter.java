package org.streampipes.pe.sources.samples;

import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.Gson;
import org.streampipes.commons.config.old.ConfigurationManager;
import org.streampipes.pe.sources.samples.friction.FrictionRawEvent;
import org.streampipes.pe.sources.samples.friction.FrictionValue;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * Created by riemer on 10.11.2016.
 */
public class FrictionCsvToJsonConverter {

    private static final String FrictionCsvInputFile = "output.csv";
    private static final String FrictionJsonOutputFile = "friction-json.txt";
    private static final String FrictionDirectory = "data/";

    public static void main(String[] args) {
        try {


            CSVReader reader = new CSVReader(new FileReader(buildFile()));
            PrintWriter out = new PrintWriter(buildOutputFile());

            String[] nextLine;
            int lineCount = 1;
            while ((nextLine = reader.readNext()) != null) {
                if (lineCount >= 308) out.println(makeJson(nextLine));
                lineCount++;
            }
            reader.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String makeJson(String[] nextLine) {
        Gson gson = new Gson();
        FrictionRawEvent rawEvent = new FrictionRawEvent();
        rawEvent.setStart(nextLine[0]);
        rawEvent.setEnd(nextLine[1]);
        rawEvent.setGearbox(makeFrictionValue(nextLine[2], nextLine[3]));
        rawEvent.setSwivel(makeFrictionValue(nextLine[4], nextLine[5]));

        return gson.toJson(rawEvent);

    }

    private static FrictionValue makeFrictionValue(String frictionValue, String mean) {
        FrictionValue value = new FrictionValue();
        value.setValue(Double.parseDouble(frictionValue));
        value.setMeanTemperature(Double.parseDouble(mean));
        return value;
    }

    private static File buildFile() {
        File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() + "/" + FrictionDirectory + FrictionCsvInputFile);
        return file;
    }

    private static File buildOutputFile() {
        File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() + "/" + FrictionDirectory + FrictionJsonOutputFile);
        return file;
    }
}
