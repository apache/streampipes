package de.fzi.cep.sepa.manager.generation.code;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;

public class ControllerClassGenerator {

	private SepaInvocation graph;
	
	public ControllerClassGenerator(SepaInvocation graph) {
		this.graph = graph;
	}
	
	public JCodeModel generate() throws JClassAlreadyExistsException {
		
		JCodeModel cm = new JCodeModel();
		JDefinedClass dc = cm._class("Controller");
		dc._extends(cm.ref("de.fzi.cep.sepa.runtime.flat.FlatEpDeclarer").narrow(cm.ref("ParameterClass")));
		
		JMethod declareMethod = dc.method(JMod.PUBLIC, de.fzi.cep.sepa.model.impl.graph.SepaDescription.class, "declareModel");
		declareMethod.annotate(java.lang.Override.class);
		addDeclareBody(cm, declareMethod);
		
		JMethod invokeMethod = dc.method(JMod.PUBLIC, de.fzi.cep.sepa.model.impl.Response.class, "invokeRuntime");
		invokeMethod.annotate(java.lang.Override.class);
		invokeMethod.param(de.fzi.cep.sepa.model.impl.graph.SepaInvocation.class, "graph");
		addInvokeBody(cm, invokeMethod);
		
		
				
		
		return cm;
	}

	private void addInvokeBody(JCodeModel cm, JMethod invokeMethod) {
		CodeGenerationVisitor visitor = new CodeGenerationVisitor(cm, invokeMethod, graph);
		graph.getStaticProperties().forEach(sp -> sp.accept(visitor));
		
	}

	private void addDeclareBody(JCodeModel cm, JMethod declareMethod) {
		
		JTryBlock tryBlock=declareMethod.body()._try();
		
		tryBlock
			.body()
			._return(cm
					.ref("de.fzi.cep.sepa.util.DeclarerUtils")
					.staticInvoke("descriptionFromResources")
					.arg(cm.ref("com.google.common.io.Resources")
							.staticInvoke("getResource")
								.arg("jsonldFile.jsonld"))
					.arg(JExpr.direct("de.fzi.cep.sepa.model.impl.graph.SepaDescription.class")));
		
		JCatchBlock catchBlock = tryBlock._catch(cm.ref(SepaParseException.class));
		catchBlock.param("e");
		catchBlock
				.body()
				._return(JExpr._null());
				
	}
	
	public static void main(String[] args) {
		
		SepaInvocation graph;
		try {
			String jsonld = Resources.toString(Resources.getResource("advancedtextfilter.jsonld"), Charsets.UTF_8);
			System.out.println(jsonld);
			graph = new SepaInvocation(new JsonLdTransformer().fromJsonLd(jsonld, SepaDescription.class));
			System.out.println(graph.getStaticProperties().size());
			JCodeModel cm = new ControllerClassGenerator(graph).generate();
			cm.build(new SingleStreamCodeWriter(System.out));
		
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
		} catch (JClassAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
