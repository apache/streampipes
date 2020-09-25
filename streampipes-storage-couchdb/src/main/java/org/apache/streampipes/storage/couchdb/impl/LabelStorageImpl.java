package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.labeling.Label;
import org.apache.streampipes.storage.api.ILabelStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class LabelStorageImpl extends AbstractDao<Label> implements ILabelStorage {

    public LabelStorageImpl() {
        super(Utils::getCouchDbLabelClient, Label.class);
    }

    @Override
    public List<Label> getAllLabels() {
        return findAll();
    }

    @Override
    public void storeLabel(Label label) {
        persist(label);
    }

    @Override
    public void updateLabel(Label label) {
        update(label);
    }

    @Override
    public Label getLabel(String labelId) {
        return find(labelId).orElse(new Label());
    }

    @Override
    public void deleteLabel(String labelId) {
        delete(labelId);
    }
}
