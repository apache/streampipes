import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.input.FormInput;
import org.apache.commons.io.Charsets;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by robin on 31.10.14.
 */
public class TestPipelineStorage {

    private static final Logger LOG = LoggerFactory.getLogger(TestPipelineStorage.class);


    public static void main(String[] args) throws IOException{
        URL url = Resources.getResource("TestJSON.json");
        Scanner scanner = new Scanner(new File(url.getPath()));
        String json = scanner.useDelimiter("\\Z").next();
        scanner.close();

        CouchDbClient dbClient = new CouchDbClient();
        dbClient.save(json);
        dbClient.shutdown();
    }

}
