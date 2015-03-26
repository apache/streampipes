package de.fzi.cep.sepa.esper.output.topx;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class TopXParameter extends BindingParameters{

	OrderDirection orderDirection;
	String orderByPropertyName;
	String outputPropertyName;
	int limit;
	
	
	
	public TopXParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, OrderDirection orderDirection, String orderByPropertyName, String outputPropertyName, int limit) {
		super(inName, outName, allProperties, partitionProperties);
		this.orderDirection = orderDirection;
		this.orderByPropertyName = orderByPropertyName;
		this.outputPropertyName = outputPropertyName;
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
	
	

}
