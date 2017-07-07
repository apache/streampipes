package de.fzi.cep.sepa.model.impl;

public class Response {

	private boolean success;
	
	private String elementId;
	
	private String optionalMessage;
	
	public Response(String elementId, boolean success)
	{
        this(elementId, success, "");
	}
	
	public Response(String elementId, boolean success, String optionalMessage)
	{
		this.elementId = elementId;
		this.success = success;
		this.optionalMessage = optionalMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getElementId() {
		return elementId;
	}

	public String getOptionalMessage() {
		return optionalMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result + ((optionalMessage == null) ? 0 : optionalMessage.hashCode());
		result = prime * result + (success ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Response other = (Response) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (optionalMessage == null) {
			if (other.optionalMessage != null)
				return false;
		} else if (!optionalMessage.equals(other.optionalMessage))
			return false;
		if (success != other.success)
			return false;
		return true;
	}
	
	
	
}
