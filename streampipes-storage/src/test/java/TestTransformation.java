

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.openrdf.model.Graph;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import org.streampipes.commons.Utils;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.transform.JsonLdTransformer;
import org.streampipes.storage.controller.StorageManager;

public class TestTransformation {

	public static void main(String[] args)
	{
		List<SepaDescription> sepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
		
		Graph graph;
		try {
			graph = new JsonLdTransformer().toJsonLd(sepas.get(0));
			System.out.println(Utils.asString(graph));
			
			SepaDescription sepa = new JsonLdTransformer().fromJsonLd(Utils.asString(graph), SepaDescription.class);
			System.out.println(sepa.getElementId());
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedRDFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
