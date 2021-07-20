package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.param.RetentionPolicyQueryParams;
import org.apache.streampipes.model.datalake.DataLakeRetentionPolicy;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class ShowRetentionPolicyQuery extends ParameterizedDataExplorerQuery<RetentionPolicyQueryParams, List<DataLakeRetentionPolicy>> {

    public ShowRetentionPolicyQuery(RetentionPolicyQueryParams queryParams) {
        super(queryParams);
    }


    @Override
    protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
        queryBuilder.add(showRetentionPolicyStatement());
    }

    @Override
    protected List<DataLakeRetentionPolicy> postQuery(QueryResult result) throws RuntimeException {
        List<DataLakeRetentionPolicy> policies = new ArrayList<>();
        for (List<Object> a : result.getResults().get(0).getSeries().get(0).getValues()) {
            boolean isDefault = false;
            if (a.get(4).toString().equals("true")) {
                isDefault = true;
            }
            policies.add(new DataLakeRetentionPolicy(a.get(0).toString(), a.get(1).toString(), isDefault));
        }
        return policies;
    }

    private String showRetentionPolicyStatement() {
        return "SHOW RETENTION POLICIES  ON " + "sp";
    }
}
