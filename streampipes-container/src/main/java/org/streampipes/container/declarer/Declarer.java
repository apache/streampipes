package org.streampipes.container.declarer;


import org.streampipes.model.NamedSEPAElement;

public interface Declarer<D extends NamedSEPAElement> {

	D declareModel();

}
