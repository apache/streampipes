//import com.google.common.io.Resources;
//import org.streampipes.model.client.preprocessing.Pipeline;
//import org.streampipes.storage.controller.StorageManager;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.util.Scanner;
//
//public class TestPipelineStorage {
//
//    //private static final Logger LOG = LoggerFactory.getLogger(TestPipelineStorage.class);
//
//
//    public static void main(String[] args) throws IOException{
//        URL url = Resources.getResource("TestJSON.json");
//        Scanner scanner = new Scanner(new File(url.getPath()));
//        //String json = scanner.useDelimiter("\\Z").next();
//        scanner.close();
//
//
//        //CouchDbClient dbClient = new CouchDbClient();
//        //dbClient.save(json);
//
//
//        //dbClient.shutdown();
//
//        Pipeline preprocessing = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("fa95b256-b6b0-4600-b998-6df72a19fa5b");
//        System.out.println(preprocessing.getPipelineId());
//        preprocessing.setRunning(true);
//       // System.out.println(preprocessing.getRev());
//      StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(preprocessing);
//
//        preprocessing = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("fa95b256-b6b0-4600-b998-6df72a19fa5b");
//    }
//
//}
