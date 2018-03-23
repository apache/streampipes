//import org.eclipse.rdf4j.model.Graph;
//import org.eclipse.rdf4j.repository.RepositoryException;
//import org.eclipse.rdf4j.rio.RDFHandlerException;
//import org.eclipse.rdf4j.rio.RDFParseException;
//import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
//import org.streampipes.commons.Utils;
//import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
//import org.streampipes.model.graph.DataProcessorDescription;
//import org.streampipes.serializers.jsonld.JsonLdTransformer;
//import org.streampipes.storage.controller.StorageManager;
//
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.util.List;
//
//public class TestTransformation {
//
//	public static void main(String[] args)
//	{
//		List<DataProcessorDescription> sepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
//
//		Graph graph;
//		try {
//			graph = new JsonLdTransformer().toJsonLd(sepas.get(0));
//			System.out.println(Utils.asString(graph));
//
//			DataProcessorDescription sepa = new JsonLdTransformer().fromJsonLd(Utils.asString(graph), DataProcessorDescription.class);
//			System.out.println(sepa.getElementId());
//
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidRdfException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RDFHandlerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RDFParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedRDFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RepositoryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//
//	}
//}
