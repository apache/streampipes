package de.fzi.cep.sepa.model.client.input;

import java.util.List;

public class MultipleValueInput<T extends FormInput> extends FormInput {

	private String memberType;
	
	private List<T> members;
	
	public MultipleValueInput()
	{
		super(ElementType.MULTIPLE_VALUE);
	}

	public List<T> getMembers() {
		return members;
	}

	public void setMembers(List<T> members) {
		this.members = members;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}	
	
}
