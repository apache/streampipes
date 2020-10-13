package org.apache.streampipes.rest.api;

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.model.labeling.Label;

import javax.ws.rs.core.Response;

public interface ILabel {

    Response getAllLabels();

    Response addLabel(Label label);

    Response updateLabel(Label label);

    Response deleteLabel(String labelId);

    Response getLabelsForCategory(String categoryId);

}
