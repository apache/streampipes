package org.streampipes.pe.mixed.flink.samples.filter.numericallist;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListFilter implements Serializable, FlatMapFunction<Map<String, Object>, Map<String, Object>> {

  private String propertyName;
  private List<Double> filterKeywords;
  private FilterOperation filterOperation;
  private FilterSettings filterSettings;


  public ListFilter(String propertyName, List<Double> filterKeywords, FilterOperation filterOperation, FilterSettings
   filterSettings) {
    this.propertyName = propertyName;
    this.filterKeywords = filterKeywords;
    this.filterOperation = filterOperation;
    this.filterSettings = filterSettings;
  }

  @Override
  public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
    List<Double> listValues = getListValues(in);
    Boolean filterSatisfied = false;

    if (filterSettings == FilterSettings.ANY_VALUE) {
      filterSatisfied = matchAny(listValues);
    } else {
      filterSatisfied = matchAll(listValues);
    }

    if (filterSatisfied) {
      out.collect(in);
    }
  }

  private Boolean matchAll(List<Double> listValues) {
    return listValues.stream().allMatch(this::satisfiesFilter);
  }

  private Boolean matchAny(List<Double> listValues) {
    return listValues.stream().anyMatch(this::satisfiesFilter);
  }

  private boolean satisfiesFilter(Double v) {
    if (filterOperation == FilterOperation.EQ) {
      return filterKeywords.stream().anyMatch(k -> v == k);
    } else if (filterOperation == FilterOperation.GE) {
      return filterKeywords.stream().anyMatch(k -> v >= k);
    } else if (filterOperation == FilterOperation.LE) {
      return filterKeywords.stream().anyMatch(k -> v <= k);
    } else if (filterOperation == FilterOperation.LT) {
      return filterKeywords.stream().anyMatch(k -> v < k);
    } else {
      return filterKeywords.stream().anyMatch(k -> v > k);
    }
  }

  private List<Double> getListValues(Map<String, Object> in) {
    for(String key : in.keySet()) {
      if (key.equals(propertyName)) {
        return ((List<Object>) in.get(key)).stream().map(o -> Double.parseDouble(String.valueOf(o))).collect(Collectors
                .toList());
      } else if (in.get(key) instanceof Map) {
        return getListValues((Map<String, Object>) in.get(key));
      }
    }
    return Collections.emptyList();
  }


}
