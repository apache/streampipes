package de.fzi.cep.sepa.manager.generation.code;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class ParametersClassGenerator {

	private String fullyQualifiedClassName;
	private List<FieldName> fieldNames;
	
	public ParametersClassGenerator(String fullyQualifiedClassName, List<FieldName> fieldNames) {
		this.fullyQualifiedClassName = fullyQualifiedClassName;
		this.fieldNames = fieldNames;
	}
	
	public JCodeModel generate() throws JClassAlreadyExistsException {
		
		JCodeModel cm = new JCodeModel();
		JDefinedClass dc = cm._class(fullyQualifiedClassName);
		dc._extends(cm.ref("de.fzi.cep.sepa.runtime.param.BindingParameters"));
		
		
		JMethod constructor = dc.constructor(JMod.PUBLIC);
		constructor.param(SepaInvocation.class, "graph");
		constructor.body().invoke("super").arg(JExpr.ref("graph"));
		
		for(FieldName fieldName : fieldNames) {
			
			dc.field(JMod.PRIVATE, makeClassName(cm, fieldName), fieldName.getName());
			
			constructor.body().assign(JExpr._this().ref(fieldName.getName()), JExpr.ref(fieldName.getName()));
			constructor.param(makeClassName(cm, fieldName), fieldName.getName());
			JMethod fieldGetterMethod = dc.method(JMod.PUBLIC, fieldName.getClazz(), "get" +WordUtils.capitalize(fieldName.getName()));
	        fieldGetterMethod.body()._return(JExpr.ref(fieldName.getName()));
	       
		}
		
		return cm;
	}
	
	private JClass makeClassName(JCodeModel cm, FieldName fieldName) {
		if (fieldName.isClassNarrowed()) return cm.ref(fieldName.getClazz()).narrow(fieldName.getNarrowClazz());
		else return cm.ref(fieldName.getClazz());
	}

	public static void main(String[] args) {
		FieldName fn = new FieldName("test", List.class, FieldName.class);
		FieldName fn2 = new FieldName("test2", String.class);
		try {
			
			JCodeModel cm = new ParametersClassGenerator("de.fzi.cep.TestParameters", Arrays.asList(fn, fn2)).generate();
			cm.build(new SingleStreamCodeWriter(System.out));
			
		} catch (JClassAlreadyExistsException | IOException e) {
			e.printStackTrace();
		}
	}
}
