package org.apache.streampipes.storage.api;

import org.apache.streampipes.model.labeling.Label;

import java.util.List;

public interface ILabelStorage {
    List<Label> getAllLabels();

    void storeLabel(Label label);

    void updateLabel(Label label);

    Label getLabel(String labelId);

    void deleteLabel(String labelId);
}
