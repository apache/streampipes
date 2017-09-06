package org.streampipes.performance.dataprovider;

import java.util.List;

public interface DataProvider<T> {

  List<T> getPreparedItems();
}
