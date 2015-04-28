package de.fzi.cep.sepa.esper.test;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.Graph;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestTransformation {

	public static void main(String[] args)
	{
		//List<SEPA> sepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
		
		try {
			//graph = new JsonLdTransformer().toJsonLd(sepas.get(0));
			String test = FileUtils.readFileToString(new File("src/test/resources/TestSepaSerialization.jsonld"), "UTF-8");
			//Graph graph;
			
			//System.out.println(Utils.asString(graph));
			
			SEPA sepa = new JsonLdTransformer().fromJsonLd(test, SEPA.class);
			System.out.println(sepa.getElementId());
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
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
