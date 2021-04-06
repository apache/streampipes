package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;


public enum DbDataTypes {



    BOOL("BOOL"),
    BOOLEAN("BOOLEAN"),
    TEXT("TEXT"),
    VAR_CHAR("VARCHAR255"),
    STRING("STRING"),
    DOUBLE("DOUBLE"),
    DOUBLE_PRECISION("DOUBLE PRECISION"),
    FLOAT("FLOAT"),
    REAL("REAL"),
    BIG_INT("BIGINT"),
    INT64("INT64"),
    INT32("INT32"),
    INT("INT"),
    INTEGER("INTEGER"),
    TIMESTAMP("TIMESTAMP"),
    DATE("DATE"),
    TIME("TIME"),
    DATETIME("DATETIME");

    private String sqlTerm;


    DbDataTypes(String sqlTerm) {
        this.sqlTerm = sqlTerm;
    }

    @Override
    public String toString() {
        return sqlTerm;
    }
}
