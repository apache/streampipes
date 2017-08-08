package org.streampipes.pe.processors.esper.topx;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

public class TopXParameter extends BindingParameters{

	OrderDirection orderDirection;
	String orderByPropertyName;
	String outputPropertyName;
	int limit;
	List<String> uniqueProperties;	
	
	public TopXParameter(SepaInvocation graph, OrderDirection orderDirection, String orderByPropertyName, String outputPropertyName, int limit, List<String> uniqueProperties) {
		super(graph);
		this.orderDirection = orderDirection;
		this.orderByPropertyName = orderByPropertyName;
		this.outputPropertyName = outputPropertyName;
		this.uniqueProperties = uniqueProperties;
		this.limit = limit;
	}

	public OrderDirection getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(OrderDirection orderDirection) {
		this.orderDirection = orderDirection;
	}

	public String getOrderByPropertyName() {
		return orderByPropertyName;
	}

	public void setOrderByPropertyName(String orderByPropertyName) {
		this.orderByPropertyName = orderByPropertyName;
	}

	public String getOutputPropertyName() {
		return outputPropertyName;
	}

	public void setOutputPropertyName(String outputPropertyName) {
		this.outputPropertyName = outputPropertyName;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<String> getUniqueProperties() {
		return uniqueProperties;
	}

	public void setUniqueProperties(List<String> uniqueProperties) {
		this.uniqueProperties = uniqueProperties;
	}

}
