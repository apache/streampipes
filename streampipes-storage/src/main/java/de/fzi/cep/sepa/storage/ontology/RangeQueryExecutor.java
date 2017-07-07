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
	public static final List<String> RANGE_TYPE_RDFS_LITERAL = Arrays.asList("http://www.w3.org/2001/XMLSchema#string", 
			"http://www.w3.org/2001/XMLSchema#boolean",
			"http://www.w3.org/2001/XMLSchema#integer",
			"http://www.w3.org/2001/XMLSchema#double",
			"http://www.w3.org/2001/XMLSchema#float");
	private static final String RANGE_TYPE_RDFS_QUANTITATIVE_VALUE = "http://schema.org/QuantitativeValue";
	
	
	private Range range;
	private RangeType rangeType;
	
	private String propertyId;
	private String rangeId;
	private List<String> rangeTypeRdfs;
	
	private boolean includeValues;
	private String instanceId;
		
	public RangeQueryExecutor(Repository repository, String propertyId, String rangeId, List<String> rangeTypeRdfs)
	{
		super(repository);
		this.propertyId = propertyId;
		this.rangeTypeRdfs = rangeTypeRdfs;
		this.includeValues = false;
		this.rangeId = rangeId;
		prepare();
	}
	
	public RangeQueryExecutor(Repository repository, String propertyId, String rangeId, List<String> rangeTypeRdfs, String instanceId)
	{
		super(repository);
		this.propertyId = propertyId;
		this.rangeTypeRdfs = rangeTypeRdfs;
		this.instanceId = instanceId;
		this.includeValues = true;
		this.rangeId = rangeId;
		prepare();
	}
	
	private void prepare()
	{
		prepareRangeType();
		
		if (rangeType == RangeType.PRIMITIVE) 
		{
			range = new PrimitiveRange(rangeId);
			if (includeValues)
			{
				try {
					TupleQueryResult result = executeQuery(QueryBuilder.getPrimitivePropertyValue(instanceId, propertyId));
					System.out.println(QueryBuilder.getPrimitivePropertyValue(instanceId, propertyId));
					while (result.hasNext()) 
					{ 
						BindingSet bindingSet = result.next();
						Value value = bindingSet.getValue("value");
						
						((PrimitiveRange)range).setValue(value.stringValue());
					}
				} catch (QueryEvaluationException | RepositoryException
						| MalformedQueryException e) {
					e.printStackTrace();
				}
			}
		}
		else if (rangeType == RangeType.ENUMERATION)
		{
			//TODO implement enumerated type
		}
		else if (rangeType == RangeType.QUANTITATIVE_VALUE)
		{
			range = new QuantitativeValueRange();
			if (includeValues)
			{
				try {
					TupleQueryResult result = executeQuery(QueryBuilder.getQuantitativeValueRange(propertyId));
					while (result.hasNext()) 
					{ 
						BindingSet bindingSet = result.next();
						Value minValue = bindingSet.getValue("minValue");
						Value maxValue = bindingSet.getValue("maxValue");
						Value unitCode = bindingSet.getValue("unitCode");
						
						((QuantitativeValueRange)range).setMinValue(Integer.parseInt(minValue.stringValue()));
						((QuantitativeValueRange)range).setMaxValue(Integer.parseInt(maxValue.stringValue()));
						((QuantitativeValueRange)range).setUnitCode(unitCode.stringValue());
						
						
					}
				} catch (QueryEvaluationException | RepositoryException
						| MalformedQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void prepareRangeType()
	{
		if (rangeTypeRdfs.stream().anyMatch(p -> p.startsWith(RANGE_TYPE_RDFS_ENUMERATION))) rangeType = RangeType.ENUMERATION;
		else if (rangeTypeRdfs.stream().anyMatch(p -> p.startsWith(RANGE_TYPE_RDFS_QUANTITATIVE_VALUE))) rangeType = RangeType.QUANTITATIVE_VALUE;
		else if (RANGE_TYPE_RDFS_LITERAL.stream().anyMatch(rt -> rangeId.startsWith(rt))) rangeType = RangeType.PRIMITIVE;
		else rangeType = RangeType.RDFS_CLASS;
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
