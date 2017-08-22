package org.streampipes.manager.monitoring.runtime;

import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.staticproperty.AnyStaticProperty;
import org.streampipes.model.impl.staticproperty.CollectionStaticProperty;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyNary;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.MatchingStaticProperty;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;

public class StaticPropertyVisitor implements org.streampipes.model.impl.staticproperty.StaticPropertyVisitor {

	InvocableSEPAElement invocableElement;
	
	public StaticPropertyVisitor(InvocableSEPAElement element)
	{
		this.invocableElement = element;
	}
	
	@Override
	public void visit(AnyStaticProperty p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FreeTextStaticProperty p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MappingPropertyNary p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MappingPropertyUnary p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OneOfStaticProperty p) {
		// TODO Auto-generated method stub
		
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

	
}
