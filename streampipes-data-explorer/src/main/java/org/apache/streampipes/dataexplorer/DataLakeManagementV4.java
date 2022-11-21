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

import com.google.gson.JsonObject;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.dataexplorer.param.RetentionPolicyQueryParams;
import org.apache.streampipes.dataexplorer.query.DeleteDataQuery;
import org.apache.streampipes.dataexplorer.query.EditRetentionPolicyQuery;
import org.apache.streampipes.dataexplorer.query.ShowRetentionPolicyQuery;
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.dataexplorer.v4.ProvidedQueryParams;
import org.apache.streampipes.dataexplorer.v4.params.QueryParamsV4;
import org.apache.streampipes.dataexplorer.v4.query.DataExplorerQueryV4;
import org.apache.streampipes.dataexplorer.v4.query.writer.OutputFormat;
import org.apache.streampipes.dataexplorer.v4.query.QueryResultProvider;
import org.apache.streampipes.dataexplorer.v4.query.StreamedQueryResultProvider;
import org.apache.streampipes.dataexplorer.v4.utils.DataLakeManagementUtils;
import org.apache.streampipes.model.datalake.DataLakeConfiguration;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataLakeRetentionPolicy;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataLakeManagementV4 {

    public List<DataLakeMeasure> getAllMeasurements() {
        return DataExplorerUtils.getInfos();
    }

    public DataLakeMeasure getById(String measureId) {
        return getDataLakeStorage().findOne(measureId);
    }

    public SpQueryResult getData(ProvidedQueryParams queryParams,
                                 boolean ignoreMissingData) throws IllegalArgumentException {
        return new QueryResultProvider(queryParams, ignoreMissingData).getData();
    }

    public void getDataAsStream(ProvidedQueryParams params,
                                OutputFormat format,
                                boolean ignoreMissingValues,
                                OutputStream outputStream) throws IOException {

        new StreamedQueryResultProvider(params, format, ignoreMissingValues).getDataAsStream(outputStream);
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

                return !queryResult.hasError() && queryResult.getResults().get(0).getError() == null;
            }
        }
        return false;
    }

    public SpQueryResult deleteData(String measurementID) {
        return this.deleteData(measurementID, null, null);
    }

    public SpQueryResult deleteData(String measurementID, Long startDate, Long endDate) {
        Map<String, QueryParamsV4> queryParts = DataLakeManagementUtils.getDeleteQueryParams(measurementID, startDate, endDate);
        return new DataExplorerQueryV4(queryParts).executeQuery(true);
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
            return new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("autogen", "0s"), "DEFAULT").executeQuery();
        } else {

            Integer batchSize = config.getBatchSize();
            Integer flushDuration = config.getFlushDuration();

            //
            // TODO:
            // - Implementation of parameter update for batchSize and flushDuration
            // - Updating multiple retention policies
            //

            String operation = "CREATE";
            if (existingRetentionPolicies.size() > 1) {
                operation = "ALTER";
            }
            return new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("custom", "1d"), operation).executeQuery();
        }
    }

    public List<DataLakeRetentionPolicy> getAllExistingRetentionPolicies() {
        //
        // TODO:
        // - Implementation of parameter return for batchSize and flushDuration
        //
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

    public Map<String, Object> getTagValues(String measurementId,
                                            String fields) {
        InfluxDB influxDB = DataExplorerUtils.getInfluxDBClient();
        Map<String, Object> tags = new HashMap<>();
        if (fields != null && !("".equals(fields))) {
            List<String> fieldList = Arrays.asList(fields.split(","));
            fieldList.forEach(f -> {
                String q = "SHOW TAG VALUES ON \"" + BackendConfig.INSTANCE.getInfluxDatabaseName() + "\" FROM \"" + measurementId + "\" WITH KEY = \"" + f + "\"";
                Query query = new Query(q);
                QueryResult queryResult = influxDB.query(query);
                queryResult.getResults().forEach(res -> {
                    res.getSeries().forEach(series -> {
                        if (series.getValues().size() > 0) {
                            String field = series.getValues().get(0).get(0).toString();
                            List<String> values = series.getValues().stream().map(v -> v.get(1).toString()).collect(Collectors.toList());
                            tags.put(field, values);
                        }
                    });
                });
            });
        }

        return tags;
    }

    public void updateDataLake(DataLakeMeasure measure) throws IllegalArgumentException {
        var existingMeasure = getDataLakeStorage().findOne(measure.getElementId());
        if (existingMeasure != null) {
            measure.setRev(existingMeasure.getRev());
            getDataLakeStorage().updateDataLakeMeasure(measure);
        } else {
            getDataLakeStorage().storeDataLakeMeasure(measure);
        }
    }

    public void deleteDataLakeMeasure(String elementId) throws IllegalArgumentException {
        if (getDataLakeStorage().findOne(elementId) != null) {
            getDataLakeStorage().deleteDataLakeMeasure(elementId);
        } else {
            throw new IllegalArgumentException("Could not find measure with this ID");
        }
    }

    public DataLakeMeasure addDataLake(DataLakeMeasure measure) {
        List<DataLakeMeasure> dataLakeMeasureList = getDataLakeStorage().getAllDataLakeMeasures();
        Optional<DataLakeMeasure> optional = dataLakeMeasureList.stream().filter(entry -> entry.getMeasureName().equals(measure.getMeasureName())).findFirst();

        if (optional.isPresent()) {
            DataLakeMeasure oldEntry = optional.get();
            if (!compareEventProperties(oldEntry.getEventSchema().getEventProperties(), measure.getEventSchema().getEventProperties())) {
                return oldEntry;
            }
        } else {
            measure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
            getDataLakeStorage().storeDataLakeMeasure(measure);
            return measure;
        }

        return measure;
    }

    private boolean compareEventProperties(List<EventProperty> prop1, List<EventProperty> prop2) {
        if (prop1.size() != prop2.size()) {
            return false;
        }

        return prop1.stream().allMatch(prop -> {

            for (EventProperty property : prop2) {
                if (prop.getRuntimeName().equals(property.getRuntimeName())) {

                    //primitive
                    if (prop instanceof EventPropertyPrimitive && property instanceof EventPropertyPrimitive) {
                        if (((EventPropertyPrimitive) prop)
                          .getRuntimeType()
                          .equals(((EventPropertyPrimitive) property).getRuntimeType())) {
                            return true;
                        }

                        //list
                    } else if (prop instanceof EventPropertyList && property instanceof EventPropertyList) {
                        return compareEventProperties(Collections.singletonList(((EventPropertyList) prop).getEventProperty()),
                          Collections.singletonList(((EventPropertyList) property).getEventProperty()));

                        //nested
                    } else if (prop instanceof EventPropertyNested && property instanceof EventPropertyNested) {
                        return compareEventProperties(((EventPropertyNested) prop).getEventProperties(),
                          ((EventPropertyNested) property).getEventProperties());
                    }
                }
            }
            return false;

        });
    }


    private IDataLakeStorage getDataLakeStorage() {
        return StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage();
    }
}
