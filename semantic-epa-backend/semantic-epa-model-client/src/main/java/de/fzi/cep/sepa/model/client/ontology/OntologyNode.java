package de.fzi.cep.sepa.model.client.ontology;

import de.fzi.cep.sepa.commons.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import de.fzi.cep.sepa.model.client.ontology.NodeType;

public class OntologyNode {

	private String prefix;
	private String namespace;
		
	private String id;
	private String title;
	private String icon;
	
	private List<OntologyNode> nodes;

	public OntologyNode(String id, String title, NodeType nodeType) {
		this.nodes = new ArrayList<>();
		this.id = id;
		this.title = title;
		this.icon = toIconUrl(nodeType);
	}
	
	public OntologyNode(String id, String title, String prefix, String namespace, NodeType nodeType) {
		this(id, title, nodeType);
		this.prefix = prefix;
		this.namespace = namespace;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String text) {
		this.title = text;
	}

	public List<OntologyNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<OntologyNode> children) {
		this.nodes = children;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
}
