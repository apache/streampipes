package org.apache.streampipes.storage.api;

import org.apache.streampipes.model.labeling.Category;
import org.apache.streampipes.model.labeling.Label;

import java.util.List;

public interface ICategoryStorage {

    List<Category>getAllCategories();

    String storeCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(String key);

    Category getCategory(String key);
}
