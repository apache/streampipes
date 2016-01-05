package de.fzi.cep.sepa.model.impl.staticproperty;

public interface StaticPropertyVisitor {

	public void visit(AnyStaticProperty p);
	public void visit(FreeTextStaticProperty p);
	public void visit(MappingPropertyNary p);
	public void visit(MappingPropertyUnary p);
	public void visit(OneOfStaticProperty p);
	public void visit(MatchingStaticProperty matchingStaticProperty);
	public void visit(CollectionStaticProperty p);
	public void visit(DomainStaticProperty p);

}
