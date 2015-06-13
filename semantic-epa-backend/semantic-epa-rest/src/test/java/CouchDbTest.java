import com.google.gson.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by robin on 25.05.15.
 */
public class CouchDbTest {

    static Logger LOG = LoggerFactory.getLogger(CouchDbTest.class);
    static CouchDbClient dbClient = new CouchDbClient("couchdb-users.properties");
    static String test = "inew Pipelinestuff";

    public static void main(String[] args) {

        //JsonObject user = dbClient.find(JsonObject.class, "_all_docs",
        //        new Params().addParam("key", "username").addParam("include_docs","true"));
        
        //dbClient.view("users").key(username);
        //List<JsonObject> objects = dbClient.view("users/password").key("username").includeDocs(true).query(JsonObject.class);
        //JsonObject firstUser = objects.get(0);
        //LOG.info(firstUser.get("hashedPassword").getAsString());
        updateTest();
    }

    public static void updateTest() {
            String username = "new";
            List<JsonObject> users = dbClient.view("users/username").key(username).includeDocs(true).query(JsonObject.class);
            if (users.size() != 1) throw new AuthenticationException("None or to many users with matching username");
            JsonObject user = users.get(0);
            if (user.has("pipelines")) {
                LOG.info("User has pipelines");
                JsonArray storedPipelines = user.getAsJsonArray("pipelines");
                JsonPrimitive newPipeline = new JsonPrimitive("newAddedPipeline");
                storedPipelines.add(newPipeline);
                user.add("pipelines", storedPipelines);
            } else {
                LOG.info("User has no pipelines");
                JsonArray storedPipelines = new JsonArray();
                JsonPrimitive newPipeline = new JsonPrimitive("newPipeline");
                storedPipelines.add(newPipeline);
                user.add("pipelines", storedPipelines);
            }
            dbClient.update(user);
    }

}
