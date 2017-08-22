package org.streampipes.model.impl.staticproperty;

public interface StaticPropertyVisitor {

	void visit(AnyStaticProperty p);
	void visit(FreeTextStaticProperty p);
	void visit(MappingPropertyNary p);
	void visit(MappingPropertyUnary p);
	void visit(OneOfStaticProperty p);
	void visit(MatchingStaticProperty matchingStaticProperty);
	void visit(CollectionStaticProperty p);
	void visit(DomainStaticProperty p);

}
