package org.streampipes.container.declarer;


import org.streampipes.model.base.NamedStreamPipesEntity;

public interface Declarer<D extends NamedStreamPipesEntity> {

	D declareModel();

}
