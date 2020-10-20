package org.apache.streampipes.storage.api;

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.model.labeling.Label;

import java.util.List;

public interface ILabelStorage {

    List<Label> getAllLabels();

    List<Label> getAllForCategory(String categoryId);

    void deleteAllForCategory(String categoryId);

    void storeLabel(Label label);

    Label getLabel(String labelId);

    void deleteLabel(String labelId);

    void updateLabel(Label label);

}
