package de.fzi.cep.sepa.manager.monitoring.runtime;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.staticproperty.AnyStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.MatchingStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;

public class StaticPropertyVisitor implements de.fzi.cep.sepa.model.impl.staticproperty.StaticPropertyVisitor{

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
