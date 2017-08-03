package org.streampipes.pe.sources.samples.friction;

import com.google.gson.Gson;
import org.streampipes.commons.config.old.ConfigurationManager;
import org.streampipes.messaging.EventProducer;
import org.streampipes.pe.sources.samples.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionReplay implements Runnable {

    EventProducer gearboxPublisher;
    EventProducer swivelPublisher;

    private static final String FrictionReplayFilename = "friction-json.txt";
    private static final String FrictionDirectory = "data/";

    public FrictionReplay(EventProducer gearboxProducer, EventProducer swivelProducer) {
        this.gearboxPublisher = gearboxProducer;
        this.swivelPublisher = swivelProducer;
    }

    @Override
    public void run() {
        Gson gson = new Gson();

        Optional<BufferedReader> readerOpt = Utils.getReader(buildFile());
        if (readerOpt.isPresent())

        {
            try {
                BufferedReader br = readerOpt.get();
                String line;

                while ((line = br.readLine()) != null) {
                    FrictionRawEvent rawEvent = gson.fromJson(line, FrictionRawEvent.class);
                    List<FrictionOutputEvent> outputEvents = new FrictionEventTransformer(rawEvent).transform();

                    sendEvent(gearboxPublisher, gson.toJson(outputEvents.get(0)));
                    sendEvent(swivelPublisher, gson.toJson(outputEvents.get(1)));

                    Thread.sleep(10000);
                }
            } catch (IOException e) {
                System.out.println("Could not read file");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendEvent(EventProducer publisher, String serializedFictionOutputEvent) {
        System.out.println(serializedFictionOutputEvent);
        publisher.publish(serializedFictionOutputEvent.getBytes());
    }

    private File buildFile() {
        File file = new File(ConfigurationManager.getStreamPipesConfigFileLocation() + "/" + FrictionDirectory + FrictionReplayFilename);
        return file;
    }
}
