package de.fzi.cep.sepa.model;

import java.util.Iterator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.MinCardinalityRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import de.fzi.cep.sepa.model.vocabulary.SEPA;

public class JenaModel {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		OntModel schema = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);

		OntClass prod = schema.createClass(SEPA.SEMANTICEVENTPRODUCER);
		
		OntClass agent = schema.createClass(SEPA.SEMANTICEVENTPROCESSINGAGENT);
		
		//OntClass aggregationAgent = schema.createClass("AggregationAgent");
	
		OntProperty p = schema.createOntProperty(SEPA.HASDESCRIPTION);
		
		Restriction r = schema.createMinCardinalityRestriction(null, p, 2);
		
		
		schema.add(prod, RDFS.subClassOf, r);
		
		schema.createIndividual(SEPA.EVENT, prod);
		
		//schema.add(r);
		//schema.add(aggregationAgent, RDFS.subClassOf, agent);
		
		//schema.add(prod, RDFS.subClassOf, restriction);
		//schema.add(prod, OWL.disjointWith, agent);
		
		
		/*
		schema.createMinCardinalityRestriction(null,
				schema.createOntProperty(SEPA.HASOUTPUTSTRATEGY), 2);
		schema.createMaxCardinalityRestriction(null,
				schema.createOntProperty(SEPA.HASOUTPUTSTRATEGY), 2);
		*/
		// sep.add

		// sep.addProperty(RDFS.subClassOf, restriction);

		// sep.addProperty(RDFS.subClassOf, OWL.Thing);
		// sep.addProperty(schema.createProperty(SEPA.HASSCHEMA), eventSchema);
		// sep.addProperty(arg0, arg1)
		schema.write(System.out);


		ValidityReport report = schema.validate();

		Iterator<Report> it = report.getReports();
		System.out.println(report.isValid());
		while (it.hasNext()) {
			System.out.println(it.next().getDescription());
		}
		
		//test();
	}

	public static void test() {
		OntModel m0 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		OntModel m1 = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);

		String NS = "http://example.org/test#";
		OntClass c0 = m1.createClass(NS + "C0");
		OntClass c1 = m1.createClass(NS + "C1");

		// :i rdf:type :c0, :c1.
		Individual i = m1.createIndividual(NS + "i", c0);
		i.addRDFType(c1);

		// disjoint classes
		m0.add(c0, OWL.disjointWith, c1);

		checkValid(m0, "m0, before");
		checkValid(m1, "m1, before");

		m1.addSubModel(m0);

		checkValid(m0, "m0, after");
		checkValid(m1, "m1, after");
	}

	private static void checkValid(OntModel m, String msg) {
		ValidityReport r = m.validate();
		String v = (r == null) ? "null" : Boolean.toString(r.isValid());

		System.out.println("Model " + msg + " isValid() => " + v);
	}

}
