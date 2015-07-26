package de.fzi.cep.sepa.model.client.ontology;

import de.fzi.cep.sepa.commons.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import de.fzi.cep.sepa.model.client.ontology.NodeType;

public class OntologyNode {

	private String id;
	private String text;
	private String icon;
	
	private List<OntologyNode> children;

	public OntologyNode(String id, String text, NodeType nodeType) {
		this.children = new ArrayList<>();
		this.id = id;
		this.text = text;
		this.icon = toIconUrl(nodeType);
	}
	
	private String toIconUrl(NodeType nodeType)
	{
		return "Test";
		//return Configuration.getInstance().WEBAPP_BASE_URL + Configuration.getInstance().CONTEXT_PATH + "/img/" +nodeType.name() +".png";
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<OntologyNode> getChildren() {
		return children;
	}

	public void setChildren(List<OntologyNode> children) {
		this.children = children;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
