package org.streampipes.manager.generation.code;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.staticproperty.AnyStaticProperty;
import org.streampipes.model.impl.staticproperty.CollectionStaticProperty;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyNary;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.MatchingStaticProperty;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticPropertyVisitor;
import org.streampipes.model.util.SepaUtils;

public class CodeGenerationVisitor implements StaticPropertyVisitor {

	private InvocableSEPAElement graph;
	private JCodeModel cm;
	private JMethod method;
	private List<FieldName> fieldNames;
	
	public CodeGenerationVisitor(JCodeModel cm, JMethod method, InvocableSEPAElement graph) {
		this.graph = graph;
		this.cm = cm;
		this.method = method;
		this.fieldNames = new ArrayList<>();
	}
	
	@Override
	public void visit(AnyStaticProperty p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FreeTextStaticProperty p) {
		
		// SepaUtils.getFreeTextStaticPropertyValue(graph, internalName)
		
		JVar variable = method
				.body()
				.decl(cm.ref(String.class), toCamelCase(p.getInternalName()))
				.init(cm
						.ref(SepaUtils.class)
						.staticInvoke("getFreeTextStaticPropertyValue")
						.arg(JExpr.ref("graph"))
						.arg(p.getInternalName()));
		
		fieldNames.add(new FieldName(variable.name(), String.class));
	}

	@Override
	public void visit(MappingPropertyNary p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MappingPropertyUnary p) {
		// String propertyName = SepaUtils.getMappingPropertyName(graph, "number-mapping");
		
		JVar variable = method
				.body()
				.decl(cm.ref(String.class), toCamelCase(p.getInternalName()))
				.init(cm
						.ref(SepaUtils.class)
						.staticInvoke("getMappingPropertyName")
						.arg(JExpr.ref("graph"))
						.arg(p.getInternalName()));
		
		fieldNames.add(new FieldName(variable.name(), String.class));	
	}


	@Override
	public void visit(OneOfStaticProperty p) {
		// String operation = SepaUtils.getOneOfProperty(graph, "operation");
	
		JVar variable = method
				.body()
				.decl(cm.ref(String.class), toCamelCase(p.getInternalName()))
				.init(cm
						.ref(SepaUtils.class)
						.staticInvoke("getOneOfProperty")
						.arg(JExpr.ref("graph"))
						.arg(p.getInternalName()));
		
		fieldNames.add(new FieldName(variable.name(), String.class));	
		
	}

	@Override
	public void visit(MatchingStaticProperty matchingStaticProperty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CollectionStaticProperty p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(DomainStaticProperty p) {
		// TODO Auto-generated method stub
		
	}
	
	public JCodeModel getGeneratedCodeModel() {
		return cm;
	}
	
	private String toCamelCase(String name) {
		 return WordUtils.uncapitalize(WordUtils.capitalizeFully(name, "-".toCharArray()).replaceAll("-", ""));
	}

}
