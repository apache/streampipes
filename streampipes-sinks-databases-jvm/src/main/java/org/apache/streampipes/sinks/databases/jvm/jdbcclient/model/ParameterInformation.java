package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

public class ParameterInformation {

    private int index;
    private DbDataTypes dataType;

    public ParameterInformation(final int index, final DbDataTypes dataType) {
        this.index = index;
        this.dataType = dataType;
    }

    public DbDataTypes getDataType() {
        return dataType;
    }

    public int getIndex() {
        return index;
    }
}
