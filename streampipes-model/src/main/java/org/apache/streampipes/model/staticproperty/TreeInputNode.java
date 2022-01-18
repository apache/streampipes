package org.apache.streampipes.model.staticproperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeInputNode {

  private boolean dataNode;
  private boolean selected;
  private String nodeName;
  private String internalNodeName;

  List<TreeInputNode> children;

  public TreeInputNode() {
    this.children = new ArrayList<>();
  }

  public TreeInputNode(TreeInputNode other) {
    this.dataNode = other.isDataNode();
    this.selected = other.isSelected();
    this.nodeName = other.getNodeName();
    this.internalNodeName = other.getInternalNodeName();
    this.children = other.getChildren().stream().map(TreeInputNode::new).collect(Collectors.toList());
  }

  public boolean hasChildren() {
    return children.size() > 0;
  }

  public List<TreeInputNode> getChildren() {
    return children;
  }

  public void setChildren(List<TreeInputNode> children) {
    this.children = children;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public String getInternalNodeName() {
    return internalNodeName;
  }

  public void setInternalNodeName(String internalNodeName) {
    this.internalNodeName = internalNodeName;
  }

  public boolean isDataNode() {
    return dataNode;
  }

  public void setDataNode(boolean dataNode) {
    this.dataNode = dataNode;
  }

  public void addChild(TreeInputNode node) {
    this.children.add(node);
  }
}
