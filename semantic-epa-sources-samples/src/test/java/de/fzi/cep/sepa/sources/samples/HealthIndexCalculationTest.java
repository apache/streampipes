package de.fzi.cep.sepa.sources.samples;

import com.google.gson.Gson;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.flink.samples.healthindex.HealthIndexCalculationFormulas2;
import de.fzi.cep.sepa.sources.samples.friction.FrictionRawEvent;
import de.fzi.cep.sepa.sources.samples.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.util.Optional;

/**
 * Created by riemer on 13.11.2016.
 */
public class HealthIndexCalculationTest {

    private static final String FrictionReplayFilename = "friction-json.txt";
    private static final String FrictionDirectory = "data/";


    private static final Integer power = 10;
    private static final Integer deltacx = 10;
    private static final Integer divider = 100;

    private static final Double stddev = 0.006994978;
    private static final Double average = 0.018200901668492;

    public static void main(String[] args) {

        try {
            Gson gson = new Gson();
            Double oldValue = -1.0;

            Optional<BufferedReader> readerOpt = Utils.getReader(buildFile());
            if (readerOpt.isPresent()) {
                BufferedReader br = readerOpt.get();
                String line;

                while ((line = br.readLine()) != null) {
                    FrictionRawEvent rawEvent = gson.fromJson(line, FrictionRawEvent.class);
                    if (oldValue != -1.0) {

                        Double ratec = HealthIndexCalculationFormulas2
                                .calculateRateC(oldValue, rawEvent.getGearbox().getValue(), deltacx, stddev);

                        Double deltac = HealthIndexCalculationFormulas2
                                .calculateDeltaC(rawEvent.getGearbox().getValue(), average, stddev, deltacx);

                        Double part1 = HealthIndexCalculationFormulas2
                                .calculatePart1(deltac, divider, power);

                        Double part2 = HealthIndexCalculationFormulas2
                                .calculatePart2(deltac);

                        Double exp = HealthIndexCalculationFormulas2
                                .calculateExp(deltac, part1, part2);

                        Double hi = HealthIndexCalculationFormulas2
                                .calculateHealthIndex(exp);

                        System.out.println(hi);
                    }
                    oldValue = rawEvent.getGearbox().getValue();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File buildFile() {
        File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() + "/" + FrictionDirectory + FrictionReplayFilename);
        return file;
    }

}
