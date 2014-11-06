package de.fzi.cep.sepa.rest.test;

import com.google.common.io.Resources;
import de.fzi.cep.sepa.model.client.Pipeline;
import org.lightcouch.CouchDbClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by robin on 31.10.14.
 */
public class TestPipelineStorage {

    public static void main(String[] args) throws FileNotFoundException {
        URL url = Resources.getResource("TestJSON.json");
        Scanner scanner = new Scanner(new File(url.getPath()));
        String json = scanner.useDelimiter("\\Z").next();
        scanner.close();

        System.out.println(json);
        new de.fzi.cep.sepa.rest.Pipeline().addPipelines(json);

        //Pipeline pipeline = Utils.getGson().fromJson(json, Pipeline.class);
    }
}
