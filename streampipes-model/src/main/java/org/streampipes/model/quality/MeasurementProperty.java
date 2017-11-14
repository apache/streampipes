package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.SSN;

import javax.persistence.Entity;

@RdfsClass(SSN.MEASUREMENT_PROPERTY)
@Entity
public class MeasurementProperty extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 8527800469513813552L;

	public MeasurementProperty() {
		super();
	}
	
	public MeasurementProperty(MeasurementProperty other) {
		super(other);
	}

}
