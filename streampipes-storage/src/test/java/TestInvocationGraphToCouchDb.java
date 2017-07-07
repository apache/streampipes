import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.storage.impl.SepaInvocationStorageImpl;


public class TestInvocationGraphToCouchDb {

	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException, RDFHandlerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException
	{
		//SepaDescription invocationGraph = new SepaDescription(new JsonLdTransformer().fromJsonLd(FileUtils.readFileToString(new File("src/test/resources/sepa-description-graph-sample.jsonld"), "UTF-8"), SepaDescription.class));
		SepaInvocation invocationGraph = new SepaInvocation(new JsonLdTransformer().fromJsonLd(FileUtils.readFileToString(new File("src/test/resources/sepa-invocation-graph-sample.jsonld"), "UTF-8"), SepaInvocation.class));
		
		
		//System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(invocationGraph)));
		System.out.println(de.fzi.cep.sepa.model.util.GsonSerializer.getGson().toJson(invocationGraph));
		new SepaInvocationStorageImpl().storeSepaInvocation(invocationGraph);
		
		SepaInvocation invocation2 = new SepaInvocationStorageImpl().getSepaInvovation("2b1fe4a1422d4ab8b4df7bc916d7364c");
		
	}
}
