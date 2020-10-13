package org.apache.streampipes.rest.api;

import org.apache.streampipes.model.labeling.Category;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public interface ICategory {

    Response getAll();

    Response getCategory(String categoryId);

    Response add(Category category);

    Response delete(String key);

    Response update(String categoryId, Category category);

}
