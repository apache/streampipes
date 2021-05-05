package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.param.RetentionPolicyQueryParams;
import org.influxdb.dto.QueryResult;

public class EditRetentionPolicyQuery extends ParameterizedDataExplorerQuery<RetentionPolicyQueryParams, String> {

    private static final String CREATE_OPERATOR = "CREATE";
    private static final String ALTER_OPERATOR = "ALTER";
    private static final String DROP_OPERATOR = "DROP";
    private static final String RESET_OPERATOR = "DEFAULT";

    private String operationToPerform;

    public EditRetentionPolicyQuery(RetentionPolicyQueryParams queryParams, String operation) {
        super(queryParams);
        this.operationToPerform = operation;
    }


    @Override
    protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
        if (this.operationToPerform.equals(CREATE_OPERATOR)) {
            queryBuilder.add(createRetentionPolicyStatement(params.getIndex()));
        } else if (this.operationToPerform.equals(ALTER_OPERATOR)) {
            queryBuilder.add(alterRetentionPolicyStatement(params.getIndex()));
        } else if (this.operationToPerform.equals(DROP_OPERATOR)) {
            queryBuilder.add(dropRetentionPolicyStatement(params.getIndex()));
        } else if (this.operationToPerform.equals(RESET_OPERATOR)) {
            queryBuilder.add(resetRetentionPolicyStatement());
        }

    }

    @Override
    protected String postQuery(QueryResult result) throws RuntimeException {
        return result.toString();
    }

    private String createRetentionPolicyStatement(String index) {
        return "CREATE RETENTION POLICY " + index + " ON " + "sp DURATION " + params.getDurationLiteral() + " REPLICATION 1 DEFAULT";
    }

    private String alterRetentionPolicyStatement(String index) {
        return "ALTER RETENTION POLICY " + index + " ON " + "sp DURATION " + params.getDurationLiteral() + " REPLICATION 1 DEFAULT";
    }

    private String dropRetentionPolicyStatement(String index) {
        return "DROP RETENTION POLICY " + index + " ON " + "sp";
    }

    private String resetRetentionPolicyStatement() {
        return "ALTER RETENTION POLICY " + "autogen" + " ON " + "sp DURATION " + "0s" + " REPLICATION 1 DEFAULT";
    }

}
