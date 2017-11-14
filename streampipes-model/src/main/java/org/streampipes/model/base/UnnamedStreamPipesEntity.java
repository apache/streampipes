package org.streampipes.model.base;


import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.RdfId;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.vocabulary.StreamPipes;

/**
 * unnamed SEPA elements (that do not require any readable identifier)
 *
 */
public abstract class UnnamedStreamPipesEntity extends AbstractStreamPipesEntity {
	
	private static final long serialVersionUID = 8051137255998890188L;
	
	private static final String prefix = "urn:streampipes.org:spi:";

	@RdfId
	@RdfProperty(StreamPipes.HAS_ELEMENT_NAME)
	private String elementName;

	private String elementId;
	
	public UnnamedStreamPipesEntity()
	{
		super();
		this.elementName = prefix + this.getClass().getSimpleName().toLowerCase() +":" +RandomStringUtils.randomAlphabetic(6);
        this.elementId = elementName;
	}
	
	public UnnamedStreamPipesEntity(UnnamedStreamPipesEntity other)
	{
		super();
		this.elementName = other.getElementName();
        this.elementId = other.getElementId();
	}
	
	public UnnamedStreamPipesEntity(String elementName)
	{
		super();
		this.elementName = elementName;
	}

	public String getElementName() {

		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementId()
	{
		return elementName;
	}

	public void setElementId(String elementId) {
		this.elementName = elementId;
	}
}
