package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.vocabulary.XSD;

public class DataTypeNames {


    public static String getInteger(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case MY_SQL:
            case INFLUX_DB:
                return  "INT";
            case POSTGRESQL:
                return  "INTEGER";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support integer values.");
    }

    public static String getLong(SupportedDbEngines sqlEngine) throws SpRuntimeException{
        switch (sqlEngine) {
            case MY_SQL:
            case POSTGRESQL:
                return  "BIGINT";
            case INFLUX_DB:
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support long values.");
    }

    public static String getFloat(SupportedDbEngines sqlEngine) throws SpRuntimeException{
        switch (sqlEngine) {
            case MY_SQL:
            case INFLUX_DB:
                return "FLOAT";
            case POSTGRESQL:
                return "REAL";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support float values.");
    }

    public static String getDouble(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case MY_SQL:
            case POSTGRESQL:
                return "DOUBLE PRECISION";
            case INFLUX_DB:
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support double values.");
    }

    /**
     * Returns the SQL data type name for a short string (up to 255 chars) for the given {@link SupportedDbEngines}
     * @param sqlEngine
     * @return
     * @throws SpRuntimeException
     */
    public static String getShortString(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case MY_SQL:
            case POSTGRESQL:
                return "VARCHAR(255)";
            case INFLUX_DB:
                return "STRING";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support short strings.");
    }

    /**
     * Returns the SQL data type name for a long string (more than to 255 chars) for the given {@link SupportedDbEngines}
     * @param sqlEngine
     * @return
     * @throws SpRuntimeException
     */
    public static String getLongString(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case MY_SQL:
            case POSTGRESQL:
                return "TEXT";
            case INFLUX_DB:
                return getShortString(sqlEngine);
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support long strings.");
    }

    public static String getBoolean(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case MY_SQL:
            case POSTGRESQL:
                return "BOOLEAN";
            case INFLUX_DB:
                return "BOOL";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support boolean values.");
    }

    public static String getTimestamp(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case INFLUX_DB:
            case MY_SQL:
            case POSTGRESQL:
                return "TIMESTAMP";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support timestamps.");
    }

    public static String getDate(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case MY_SQL:
            case POSTGRESQL:
                return "DATE";
            case INFLUX_DB:
                return "TIME";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support dates.");
    }

    public static String getTime(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case INFLUX_DB:
            case MY_SQL:
            case POSTGRESQL:
                return "TIME";
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support time.");
    }

    public static String getDatetime(SupportedDbEngines sqlEngine) throws SpRuntimeException {
        switch (sqlEngine) {
            case INFLUX_DB:
                return "TIME";
            case MY_SQL:
                return "DATETIME";
            case POSTGRESQL:
        }
        throw new SpRuntimeException("Database engine " + sqlEngine + " does not support datetime.");
    }

    public static String getFromObject(final Object o, SupportedDbEngines sqlEngine) {

        if (o instanceof Integer) {
            return getInteger(sqlEngine);
        } else if (o instanceof Long) {
           return getLong(sqlEngine);
        } else if (o instanceof Float) {
            return getFloat(sqlEngine);
        } else if (o instanceof Double) {
            return getDouble(sqlEngine);
        } else if (o instanceof Boolean) {
            return getBoolean(sqlEngine);
        } else {
            return getLongString(sqlEngine);
        }
    }

    public static String getFromUri(final String uri, SupportedDbEngines sqlEngine) {
        if (uri.equals(XSD._integer.toString())) {
            return getInteger(sqlEngine);
        } else if (uri.equals(XSD._long.toString())) {
            return getLong(sqlEngine);
        } else if (uri.equals(XSD._float.toString())) {
            return getFloat(sqlEngine);
        } else if (uri.equals(XSD._double.toString())) {
            return getDouble(sqlEngine);
        } else if (uri.equals(XSD._boolean.toString())) {
            return getBoolean(sqlEngine);
        } else {
            return getLongString(sqlEngine);
        }
    }

}
