package de.fzi.cep.sepa.storage.ontology;

import java.util.Arrays;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.PrimitiveRange;
import de.fzi.cep.sepa.model.client.ontology.QuantitativeValueRange;
import de.fzi.cep.sepa.model.client.ontology.Range;
import de.fzi.cep.sepa.model.client.ontology.RangeType;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;

public class RangeQueryExecutor extends QueryExecutor {

	private static final String RANGE_TYPE_RDFS_ENUMERATION = "http://sepa.event-processing.org/sepa#Enumeration";
	private static final List<String> RANGE_TYPE_RDFS_LITERAL = Arrays.asList("http://www.w3.org/2001/XMLSchema#string", 
			"http://www.w3.org/2001/XMLSchema#boolean",
			"http://www.w3.org/2001/XMLSchema#integer",
			"http://www.w3.org/2001/XMLSchema#double",
			"http://www.w3.org/2001/XMLSchema#float");
	private static final String RANGE_TYPE_RDFS_QUANTITATIVE_VALUE = "http://schema.org/QuantitativeValue";
	
	
	private Range range;
	private RangeType rangeType;
	
	private String propertyId;
	private String rangeTypeRdfs;
		
	public RangeQueryExecutor(Repository repository, String propertyId, String rangeTypeRdfs)
	{
		super(repository);
		this.propertyId = propertyId;
		this.rangeTypeRdfs = rangeTypeRdfs;
		prepare();
	}
	
	private void prepare()
	{
		prepareRangeType();
		
		if (rangeType == RangeType.PRIMITIVE) range = new PrimitiveRange(rangeTypeRdfs);
		else if (rangeType == RangeType.ENUMERATION)
		{
			//TODO implement enumerated type
		}
		else if (rangeType == RangeType.QUANTITATIVE_VALUE)
		{
			try {
				TupleQueryResult result = executeQuery(QueryBuilder.getQuantitativeValueRange(propertyId));
				while (result.hasNext()) 
				{ 
					BindingSet bindingSet = result.next();
					Value minValue = bindingSet.getValue("minValue");
					Value maxValue = bindingSet.getValue("maxValue");
					Value unitCode = bindingSet.getValue("unitCode");
					
					range = new QuantitativeValueRange(Integer.parseInt(minValue.toString()), Integer.parseInt(maxValue.toString()), unitCode.toString());
				}
			} catch (QueryEvaluationException | RepositoryException
					| MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void prepareRangeType()
	{
		if (rangeTypeRdfs.startsWith(RANGE_TYPE_RDFS_ENUMERATION)) rangeType = RangeType.ENUMERATION;
		else if (rangeTypeRdfs.startsWith(RANGE_TYPE_RDFS_QUANTITATIVE_VALUE)) rangeType = RangeType.QUANTITATIVE_VALUE;
		else rangeType = RangeType.PRIMITIVE;
	}
	
	public Range getRange()
	{
		return range;
	}
	
	public RangeType getRangeType()
	{
		return rangeType;
	}
	
}
