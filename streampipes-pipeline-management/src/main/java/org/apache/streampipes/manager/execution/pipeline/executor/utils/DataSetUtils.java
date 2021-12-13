package org.apache.streampipes.manager.execution.pipeline.executor.utils;

import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataSetUtils {

    public static List<SpDataSet> filterDataSetsById(List<SpDataSet> dataSets,
                                                     Set<String> dataSetIds) {
        //TODO: Check if DatasetInvocationId is the correct id to check for
        return dataSets.stream().
                filter(dataSet -> dataSetIds.contains(dataSet.getDatasetInvocationId()))
                .collect(Collectors.toList());
    }

}
