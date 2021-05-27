package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TableDescription {

    private final String name;
    private boolean tableExists;
    private HashMap<String, DbDataTypes> dataTypesHashMap;

    public TableDescription(String name) {
        this.name = name;
        this.tableExists = false;
    }

    public void extractTableInformation(PreparedStatement preparedStatement, Connection connection,
                                           String queryString, String[] queryParameter) throws SpRuntimeException {

        ResultSet resultSet = null;
        this.dataTypesHashMap = new HashMap<String, DbDataTypes>();

        try {

            preparedStatement = connection.prepareStatement(queryString);

            for (int i = 1; i <= queryParameter.length; i++) {
                preparedStatement.setString(i, queryParameter[i - 1]);
            }

            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                do {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    DbDataTypes dataType = DbDataTypes.valueOf(resultSet.getString("DATA_TYPE").toUpperCase());
                    this.dataTypesHashMap.put(columnName, dataType);
                } while (resultSet.next());
            } else {
                throw new SpRuntimeException("Database or Table does nit exist.");
            }
        } catch (SQLException e) {
            throw new SpRuntimeException("SqlException: " + e.getMessage() + ", Error code: " + e.getErrorCode() +
                    ", SqlState: " + e.getSQLState());
        } finally {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
            }
        }
    }

    public boolean tableExists() {
        return tableExists;
    }

    public HashMap<String, DbDataTypes> getDataTypesHashMap() {
        return dataTypesHashMap;
    }

    public String getName() {
        return name;
    }

    public void setDataTypesHashMap(HashMap<String, DbDataTypes> dataTypesHashMap) {
        this.dataTypesHashMap = dataTypesHashMap;
    }

    public void putDataTypeHashMap(String name, DbDataTypes dataType){
        this.dataTypesHashMap.put(name, dataType);
    }

    public void setTableExists(){
        this.tableExists = true;
    }

    public void setTableMissing(){
        this.tableExists = false;
    }
}
