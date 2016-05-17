package de.fzi.cep.sepa.model.client.input;

import java.util.List;

import de.fzi.cep.sepa.model.client.StaticProperty;

public class MultipleValueInput extends FormInput {

	private String memberType;
	
	private List<StaticProperty> members;
	
	public MultipleValueInput()
	{
		super(ElementType.MULTIPLE_VALUE);
	}

	public List<StaticProperty> getMembers() {
		return members;
	}

	public void setMembers(List<StaticProperty> members) {
		this.members = members;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}	
	
}
