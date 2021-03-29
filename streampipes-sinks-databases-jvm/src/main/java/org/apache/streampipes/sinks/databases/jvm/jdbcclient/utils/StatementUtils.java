package org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDataTypeFactory;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.ParameterInformation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementUtils {

    /**
     * Sets the value in the prepardStatement {@code ps}
     *
     * @param p     The needed info about the parameter (index and type)
     * @param value The value of the object, which should be filled in the
     * @param ps    The prepared statement, which will be filled
     * @throws SpRuntimeException When the data type in {@code p} is unknown
     * @throws SQLException       When the setters of the statement throw an
     *                            exception (e.g. {@code setInt()})
     */
    public static void setValue(ParameterInformation p, Object value, PreparedStatement ps)
            throws SQLException, SpRuntimeException {
        switch (DbDataTypeFactory.getDataType(p.getDataType())) {
            case Integer:
                ps.setInt(p.getIndex(), (Integer) value);
                break;
            case Long:
                ps.setLong(p.getIndex(), (Long) value);
                break;
            case Float:
                ps.setFloat(p.getIndex(), (Float) value);
                break;
            case Double:
                ps.setDouble(p.getIndex(), (Double) value);
                break;
            case Boolean:
                ps.setBoolean(p.getIndex(), (Boolean) value);
                break;
            case String:
                ps.setString(p.getIndex(), value.toString());
                break;
            case Number:
            case Sequence:
            default:
                throw new SpRuntimeException("Unknown SQL datatype");
        }
    }
}
