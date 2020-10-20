package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.storage.api.ICategoryStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.CouchDbClient;

import java.util.List;
import java.util.function.Supplier;

public class CategoryStorageImpl extends AbstractDao<Category> implements ICategoryStorage {
    public CategoryStorageImpl() {
        super(Utils::getCouchDbCategoryClient, Category.class);
    }

    @Override
    public List<Category> getAllCategories() {
        return findAll();
    }

    @Override
    public void storeCategory(Category category) {
        persist(category);
    }

    @Override
    public void updateCategory(Category category) {
        update(category);
    }

    @Override
    public void deleteCategory(String key) {
        delete(key);
    }

    @Override
    public Category getCategory(String key) {
        return find(key).orElse(null);
    }
}
