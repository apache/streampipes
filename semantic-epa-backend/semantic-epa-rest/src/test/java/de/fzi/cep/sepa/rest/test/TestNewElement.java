package de.fzi.cep.sepa.rest.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.openrdf.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonSyntaxException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.model.util.GsonSerializer;

public class TestNewElement {

	public static void main(String[] args) throws JsonSyntaxException, IOException, RDFHandlerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException
	{
		SepaDescription desc = GsonSerializer.getGsonWithoutIds().fromJson(Resources.toString(Resources.getResource("new-element.json"), Charsets.UTF_8), SepaDescription.class);
		
		System.out.println(GsonSerializer.getGsonWithIds().toJson(desc));
		
		System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(desc)));
	}
}
