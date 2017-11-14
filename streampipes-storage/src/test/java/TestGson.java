import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.serializers.json.GsonSerializer;
import org.streampipes.storage.controller.StorageManager;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;


public class TestGson {

	public static void main(String[] args) throws URISyntaxException, RDFHandlerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException
	{
		DataProcessorDescription desc =new DataProcessorDescription(StorageManager.INSTANCE.getSesameStorage().getSEPAById("http://frosch.fzi.de:8090/sepa/textfilter"));
		System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(desc)));
		desc.setName("Text Filter 2");
		System.out.println(desc.getRdfId().toString());
		System.out.println(desc.getClass().getCanonicalName());
		String json = GsonSerializer.getGson().toJson(desc);
		System.out.println(json);
		DataProcessorDescription desc2 = GsonSerializer.getGson().fromJson(json, DataProcessorDescription.class);
		System.out.println(desc2.getName());
		System.out.println(desc2.getRdfId().toString());
		//StorageManager.INSTANCE.getSesameStorage().deleteSEP(desc2.getRdfId().toString());
		//StorageManager.INSTANCE.getSesameStorage().storeSEPA(desc2);
	}
}
