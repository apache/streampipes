package de.fzi.cep.sepa.sources.samples.kd2;

import de.fzi.cep.sepa.adapter.kd2.builder.EventBuilder;
import de.fzi.cep.sepa.adapter.kd2.events.BiodataEvent;
import de.fzi.cep.sepa.adapter.kd2.publisher.StreamPipesPublisher;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;

import java.io.*;

/**
 * Created by riemer on 20.11.2016.
 */
public class Kd2Replay implements Runnable {

    private String kd2DataFileName;

    public Kd2Replay() {
        kd2DataFileName = ConfigurationManager.getStreamPipesConfigFileLocation() + "data/kd2.csv";
    }

    @Override
    public void run() {
        int rowCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(kd2DataFileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (rowCount > 0) {
                    String[] fields = line.split(";");
                    StreamPipesPublisher.INSTANCE.fireAllBioDataEvents(makeBiodataEvent(fields));
                }
                rowCount++;
                if (rowCount % 10 == 0) {
                    System.out.println(rowCount +" lines sent.");
                }
                Thread.sleep(1000);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BiodataEvent makeBiodataEvent(String[] fields) {
        return EventBuilder.buildBiodataEvent(Long.parseLong(fields[0]),
                Long.parseLong(fields[1]),
                Byte.parseByte(fields[2]),
                "client1",
                Double.parseDouble(fields[3]),
                Double.parseDouble(fields[4]),
                Double.parseDouble(fields[5]),
                Double.parseDouble(fields[6]));
    }

    public static void main(String[] args) {
        Kd2Replay replay = new Kd2Replay();
        Thread thread = new Thread(replay);

        thread.start();
    }
}
