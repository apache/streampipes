//import com.google.common.io.Resources;
//import org.streampipes.model.client.pipeline.Pipeline;
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
//        Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("fa95b256-b6b0-4600-b998-6df72a19fa5b");
//        System.out.println(pipeline.getPipelineId());
//        pipeline.setRunning(true);
//       // System.out.println(pipeline.getRev());
//      StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
//
//        pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("fa95b256-b6b0-4600-b998-6df72a19fa5b");
//    }
//
//}
