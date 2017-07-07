package org.streampipes.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericTreeNode<T> {

    private T data;
    private List<GenericTreeNode<T>> children;
    private GenericTreeNode<T> parent;

    public GenericTreeNode() {
        super();
        children = new ArrayList<GenericTreeNode<T>>();
    }

    public GenericTreeNode(T data) {
        this();
        setData(data);
    }

    public GenericTreeNode<T> getParent() {
        return this.parent;
    }

    public List<GenericTreeNode<T>> getChildren() {
        return this.children;
    }

    public int getNumberOfChildren() {
        return getChildren().size();
    }

    public boolean hasChildren() {
        return (getNumberOfChildren() > 0);
    }

    public void setChildren(List<GenericTreeNode<T>> children) {
        for(GenericTreeNode<T> child : children) {
           child.parent = this;
        }

        this.children = children;
    }

    public void addChild(GenericTreeNode<T> child) {
        child.parent = this;
        children.add(child);
    }

    public void addChildAt(int index, GenericTreeNode<T> child) throws IndexOutOfBoundsException {
        child.parent = this;
        children.add(index, child);
    }

    public void removeChildren() {
        this.children = new ArrayList<GenericTreeNode<T>>();
    }

    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }

    public GenericTreeNode<T> getChildAt(int index) throws IndexOutOfBoundsException {
        return children.get(index);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String toString() {
        return getData().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj == null) {
           return false;
        }
        if (getClass() != obj.getClass()) {
           return false;
        }
        GenericTreeNode<?> other = (GenericTreeNode<?>) obj;
        if (data == null) {
           if (other.data != null) {
              return false;
           }
        } else if (!data.equals(other.data)) {
           return false;
        }
        return true;
    }

    /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
    @Override
    public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result + ((data == null) ? 0 : data.hashCode());
       return result;
    }

    public String toStringVerbose() {
        String stringRepresentation = getData().toString() + ":[";

        for (GenericTreeNode<T> node : getChildren()) {
            stringRepresentation += node.getData().toString() + ", ";
        }

        //Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
        Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(stringRepresentation);

        stringRepresentation = matcher.replaceFirst("");
        stringRepresentation += "]";

        return stringRepresentation;
    }
}
