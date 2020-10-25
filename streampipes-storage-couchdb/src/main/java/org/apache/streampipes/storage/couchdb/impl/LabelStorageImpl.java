package org.apache.streampipes.storage.couchdb.impl;

import com.google.gson.JsonObject;
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
    public String storeLabel(Label label) {
        return persist(label).b;
    }

    @Override
    public Label getLabel(String labelId) {
        return find(labelId).orElse(new Label());
    }

    @Override
    public void deleteLabel(String labelId) {
        delete(labelId);
    }

    @Override
    public void updateLabel(Label label) {
        update(label);
    }

    @Override
    public List<Label> getAllForCategory(String categoryId) {
        return couchDbClientSupplier.get()
                .view("categoryId/categoryId")
                .key(categoryId)
                .includeDocs(true)
                .query(clazz);
    }

    @Override
    public void deleteAllForCategory(String categoryId) {
        List<Label> labelsForCategory = getAllForCategory(categoryId);
        for (Label label: labelsForCategory) {
            delete(label.getId());
        }
    }
}
