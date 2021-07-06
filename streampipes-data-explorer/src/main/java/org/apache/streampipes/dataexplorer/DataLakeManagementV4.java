/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.dataexplorer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.streampipes.dataexplorer.param.RetentionPolicyQueryParams;
import org.apache.streampipes.dataexplorer.query.DeleteDataQuery;
import org.apache.streampipes.dataexplorer.query.EditRetentionPolicyQuery;
import org.apache.streampipes.dataexplorer.query.ShowRetentionPolicyQuery;
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.dataexplorer.v4.params.QueryParamsV4;
import org.apache.streampipes.dataexplorer.v4.query.DataExplorerQueryV4;
import org.apache.streampipes.dataexplorer.v4.utils.DataLakeManagementUtils;
import org.apache.streampipes.model.datalake.DataLakeConfiguration;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataLakeRetentionPolicy;
import org.apache.streampipes.model.datalake.DataResult;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.influxdb.dto.QueryResult;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class DataLakeManagementV4 {

    public List<DataLakeMeasure> getAllMeasurements() {
        List<DataLakeMeasure> allMeasurements = DataExplorerUtils.getInfos();
        return allMeasurements;
    }

    public DataResult getData(String measurementID, String columns, Long startDate, Long endDate, Integer page, Integer limit, Integer offset, String groupBy, String order, String aggregationFunction, String timeInterval) {
        Map<String, QueryParamsV4> queryParts = DataLakeManagementUtils.getSelectQueryParams(measurementID, columns, startDate, endDate, page, limit, offset, groupBy, order, aggregationFunction, timeInterval);
        return new DataExplorerQueryV4(queryParts).executeQuery();
    }

    public void getDataAsStream(String measurementID, String columns, Long startDate, Long endDate, Integer page, Integer limit, Integer offset, String groupBy, String order, String aggregationFunction, String timeInterval, String format, OutputStream outputStream) throws IOException {
        if (limit == null) {
            limit = 500000;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        DataResult dataResult;
        //JSON
        if (format.equals("json")) {

            Gson gson = new Gson();
            int i = 0;
            if (page != null) {
                i = page;
            }

            boolean isFirstDataObject = true;

            outputStream.write(toBytes("["));
            do {
                dataResult = getData(measurementID, columns, startDate, endDate, i, limit, null, groupBy, order, aggregationFunction, timeInterval);

                if (dataResult.getTotal() > 0) {
                    for (List<Object> row : dataResult.getRows()) {
                        if (!isFirstDataObject) {
                            outputStream.write(toBytes(","));
                        }

                        //produce one json object
                        boolean isFirstElementInRow = true;
                        outputStream.write(toBytes("{"));
                        for (int i1 = 0; i1 < row.size(); i1++) {
                            Object element = row.get(i1);
                            if (!isFirstElementInRow) {
                                outputStream.write(toBytes(","));
                            }
                            isFirstElementInRow = false;
                            if (i1 == 0) {
                                try {
                                    element = formatter.parse(element.toString()).getTime();
                                } catch (ParseException e) {
                                    element = element.toString();
                                }
                            }
                            //produce json e.g. "name": "Pipes" or "load": 42
                            outputStream.write(toBytes("\"" + dataResult.getHeaders().get(i1) + "\": "
                                    + gson.toJson(element)));
                        }
                        outputStream.write(toBytes("}"));
                        isFirstDataObject = false;
                    }

                    i++;
                }
            } while (dataResult.getTotal() > 0);
            outputStream.write(toBytes("]"));

            //CSV
        } else if (format.equals("csv")) {
            int i = 0;
            if (page != null) {
                i = page;
            }

            boolean isFirstDataObject = true;

            do {
                dataResult = getData(measurementID, columns, startDate, endDate, i, limit, null, groupBy, order, aggregationFunction, timeInterval);
                //Send first header
                if (dataResult.getTotal() > 0) {
                    if (isFirstDataObject) {
                        boolean isFirst = true;
                        for (int i1 = 0; i1 < dataResult.getHeaders().size(); i1++) {
                            if (!isFirst) {
                                outputStream.write(toBytes(";"));
                            }
                            isFirst = false;
                            outputStream.write(toBytes(dataResult.getHeaders().get(i1)));
                        }
                    }
                    outputStream.write(toBytes("\n"));
                    isFirstDataObject = false;
                }

                if (dataResult.getTotal() > 0) {
                    for (List<Object> row : dataResult.getRows()) {
                        boolean isFirstInRow = true;
                        for (int i1 = 0; i1 < row.size(); i1++) {
                            Object element = row.get(i1);
                            if (!isFirstInRow) {
                                outputStream.write(toBytes(";"));
                            }
                            isFirstInRow = false;
                            if (i1 == 0) {
                                try {
                                    element = formatter.parse(element.toString()).getTime();
                                } catch (ParseException e) {
                                    element = element.toString();
                                }
                            }
                            if (element == null) {
                                outputStream.write(toBytes(""));
                            } else {
                                outputStream.write(toBytes(element.toString()));
                            }
                        }
                        outputStream.write(toBytes("\n"));
                    }
                }
                i++;
            } while (dataResult.getTotal() > 0);
        }
    }

    public boolean removeAllMeasurements() {
        List<DataLakeMeasure> allMeasurements = getAllMeasurements();

        for (DataLakeMeasure measure : allMeasurements) {
            QueryResult queryResult = new DeleteDataQuery(measure).executeQuery();
            if (queryResult.hasError() || queryResult.getResults().get(0).getError() != null) {
                return false;
            }
        }
        return true;
    }

    public boolean removeMeasurement(String measurementID) {
        List<DataLakeMeasure> allMeasurements = getAllMeasurements();
        for (DataLakeMeasure measure : allMeasurements) {
            if (measure.getMeasureName().equals(measurementID)) {
                QueryResult queryResult = new DeleteDataQuery(new DataLakeMeasure(measurementID, null)).executeQuery();

                if (queryResult.hasError() || queryResult.getResults().get(0).getError() != null) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public DataResult deleteData(String measurementID, Long startDate, Long endDate) {
        Map<String, QueryParamsV4> queryParts = DataLakeManagementUtils.getDeleteQueryParams(measurementID, startDate, endDate);
        return new DataExplorerQueryV4(queryParts).executeQuery();
    }

    public DataLakeConfiguration getDataLakeConfiguration() {
        List<DataLakeRetentionPolicy> retentionPolicies = getAllExistingRetentionPolicies();
        return new DataLakeConfiguration(retentionPolicies);
    }

    public String editMeasurementConfiguration(DataLakeConfiguration config, boolean resetToDefault) {

        List<DataLakeRetentionPolicy> existingRetentionPolicies = getAllExistingRetentionPolicies();

        if (resetToDefault) {
            if (existingRetentionPolicies.size() > 1) {
                String drop = new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("custom", "0s"), "DROP").executeQuery();
            }
            String reset = new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("autogen", "0s"), "DEFAULT").executeQuery();
            return reset;
        } else {

            Integer batchSize = config.getBatchSize();
            Integer flushDuration = config.getFlushDuration();

            /**
             * TODO:
             * - Implementation of parameter update for batchSize and flushDuration
             * - Updating multiple retention policies
             */

            String operation = "CREATE";
            if (existingRetentionPolicies.size() > 1) {
                operation = "ALTER";
            }
            String result = new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("custom", "1d"), operation).executeQuery();
            return result;
        }
    }

    public List<DataLakeRetentionPolicy> getAllExistingRetentionPolicies() {
        /**
         * TODO:
         * - Implementation of parameter return for batchSize and flushDuration
         */
        return new ShowRetentionPolicyQuery(RetentionPolicyQueryParams.from("", "0s")).executeQuery();
    }

    public boolean removeEventProperty(String measurementID) {
        boolean isSuccess = false;
        CouchDbClient couchDbClient = Utils.getCouchDbDataLakeClient();
        List<JsonObject> docs = couchDbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);

        for (JsonObject document : docs) {
            if (document.get("measureName").toString().replace("\"", "").equals(measurementID)) {
                couchDbClient.remove(document.get("_id").toString().replace("\"", ""), document.get("_rev").toString().replace("\"", ""));
                isSuccess = true;
                break;
            }
        }

        try {
            couchDbClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    private byte[] toBytes(String value) {
        return value.getBytes();
    }
}
