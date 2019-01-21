/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.jdbcclient;

import static org.streampipes.vocabulary.XSD._boolean;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.vocabulary.XSD;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// PostgreSql data:
// driver: "org.postgresql.Driver"
// urlName: "postgres"

public class JdbcClient {
	// Allowed identifiers for HadoopFileSystem
	// (https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)
	//TODO: Check default for each version
	private String allowedRegEx = "^[a-zA-Z_][a-zA-Z0-9_]*$";
	private String urlName;

	private Integer port;
	private String host;
	private String databaseName;
	private String tableName;
	private String user;
	private String password;

	private boolean tableExists = false;

	private List<EventProperty> eventProperties;
	private Logger logger;

	private Connection c = null;
	private Statement st = null;
	private PreparedStatement ps = null;

	/**
	 * The parameters in the prepared statement {@code ps} together with their index and data type
	 */
	private HashMap<String, Parameterinfo> parameters = new HashMap<>();

	/**
	 * A wrapper class for all supported SQL data types (INT, BIGINT, FLOAT, DOUBLE, VARCHAR(255)).
	 * If no matching type is found, it is interpreted as a String (VARCHAR(255))
	 */
	private enum SqlAttribute {
		INTEGER("INT"), LONG("BIGINT"), FLOAT("FLOAT"), DOUBLE("DOUBLE"), STRING("VARCHAR(255)");
		private final String sqlName;

		SqlAttribute(String s) {
			sqlName = s;
		}

		/**
		 * Tries to identify the data type of the object {@code o}. In case it is not supported, it is
		 * interpreted as a String (VARCHAR(255))
		 *
		 * @param o The object which should be identified
		 * @return An {@link SqlAttribute} of the identified type
		 */
    public static SqlAttribute getFromObject(final Object o) {
      SqlAttribute r;
      if (o instanceof Integer) {
        r = SqlAttribute.INTEGER;
      } else if (o instanceof Long) {
        r = SqlAttribute.LONG;
      } else if (o instanceof Float) {
        r = SqlAttribute.FLOAT;
      } else if (o instanceof Double) {
        r = SqlAttribute.DOUBLE;
      } else {
        r = SqlAttribute.STRING;
      }
      return r;
    }

    public static SqlAttribute getFromUri(final String s) {
      SqlAttribute r;
      if (s.equals(XSD._integer)) {
        r = SqlAttribute.INTEGER;
      } else if (s.equals(XSD._long)) {
        r = SqlAttribute.LONG;
      } else if (s.equals(XSD._float)) {
        r = SqlAttribute.FLOAT;
      } else if (s.equals(XSD._double)) {
        r = SqlAttribute.DOUBLE;
      } else {
        r = SqlAttribute.STRING;
      }
      return r;
    }

		/**
		 * Sets the value in the prepardStatement {@code ps}
		 *
		 * @param p The needed info about the parameter (index and type)
		 * @param value The value of the object, which should be filled in the
		 * @param ps The prepared statement, which will be filled
		 * @throws SpRuntimeException When the data type in {@code p} is unknown
		 * @throws SQLException When the setters of the statement throw an
     * exception (e.g. {@code setInt()})
		 */
		public static void setValue(Parameterinfo p, Object value, PreparedStatement ps)
												throws SQLException, SpRuntimeException {
			switch (p.type) {
				case INTEGER:
					ps.setInt(p.index, (Integer)value);
					break;
				case LONG:
					ps.setLong(p.index, (Long)value);
					break;
				case FLOAT:
					ps.setFloat(p.index, (Float)value);
					break;
				case DOUBLE:
					ps.setDouble(p.index, (Double)value);
					break;
				case STRING:
					ps.setString(p.index, value.toString());
					break;
				default:
					throw new SpRuntimeException("Unknown SQL Datatype");
			}
		}

		@Override
		public String toString() {
			return sqlName;
		}
	}

	/**
	 * Contains all information needed to "fill" a prepared statement (index and the data type)
	 */
	private static class Parameterinfo {
		private int index;
		private SqlAttribute type;

		private Parameterinfo(final int index, final SqlAttribute type) {
			this.index = index;
			this.type = type;
		}
	}


	public JdbcClient(List<EventProperty> eventProperties,
			String host,
			Integer post,
			String databaseName,
			String tableName,
			String user,
			String password,
			String allowedRegEx,
			String driver,
			String urlName,
			Logger logger) throws SpRuntimeException {
		this.host = host;
		this.port = post;
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.user = user;
		this.password = password;
		this.allowedRegEx = allowedRegEx;
		this.urlName = urlName;
		this.logger = logger;
		this.eventProperties = eventProperties;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new SpRuntimeException("Driver '" + driver + "' not found.");
		}

		validate();
		connect();
	}

	/**
	 * Checks whether the given {@link JdbcClient#host} and the {@link JdbcClient#databaseName}
   * are allowed
	 *
	 * @throws SpRuntimeException If either the hostname or the databasename is not allowed
	 */
	private void validate() throws SpRuntimeException {
		// Validates the database name and the attributes
    // See following link for regular expressions:
    // https://stackoverflow.com/questions/106179/regular-expression-to-match-dns-hostname-or-ip-address
		String ipRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|"
        + "[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    String hostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*"
        + "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
		//TODO: replace regex with validation method (import org.apache.commons.validator.routines.InetAddressValidator;)
    // https://stackoverflow.com/questions/3114595/java-regex-for-accepting-a-valid-hostname-ipv4-or-ipv6-address)
		/*if (!host.matches(ipRegex) && !host.matches(hostnameRegex)) {
			throw new SpRuntimeException("Error: Hostname '" + postgreSqlHost
					+ "' not allowed");
		}*/
		checkRegEx(databaseName, "Databasename");
	}

	/**
	 * Connects to the HadoopFileSystem Server and initilizes {@link JdbcClient#c} and
	 * {@link JdbcClient#st}
	 *
	 * @throws SpRuntimeException When the connection could not be established (because of a
	 * wrong identification, missing database etc.)
	 */
	private void connect() throws SpRuntimeException {
		try {
			//TODO: Create table if not exists
			String u = "jdbc:" + urlName + "://" + host + ":" + port + "/" + databaseName;
			c = DriverManager.getConnection(u, user, password);
			st = c.createStatement();

			DatabaseMetaData md = c.getMetaData();
			ResultSet rs = md.getTables(null, null, tableName, null);
			if (rs.next()) {
				//validateTable();
			} else {
				createTable();
			}
			tableExists = true;
			rs.close();

		} catch (SQLException e) {
			try {
				stop();
			} catch (SQLException sqlException) {
				throw new SpRuntimeException(e.getMessage());
			}
			throw new SpRuntimeException(e.getMessage());
		}
	}

	//TODO: Add batch support (https://stackoverflow.com/questions/3784197/efficient-way-to-do-batch-inserts-with-jdbc)
	/**
	 * Prepares a statement for the insertion of values or the
	 *
	 * @param event The event which should be saved to the Postgres table
	 * @throws SpRuntimeException When there was an error in the saving process
	 */
	public void save(final Map<String, Object> event) throws SpRuntimeException {
		if (event == null) {
			throw new SpRuntimeException("event is null");
		}
		if (!tableExists) {
			// Creates the table
			createTable();
      tableExists = true;
		}
		try {
			executePreparedStatement(event);
		} catch (SQLException e) {
			if (e.getSQLState().substring(0, 2).equals("42")) {
				// If the table does not exists (because it got deleted or something) we will try to create
				// a new one. Otherwise we do not handle the exception
				// For error codes see: https://www.postgresql.org/docs/current/errcodes-appendix.html
				//TODO: Possible problem of infinite recursion
				//TODO: Consider possible other exception handling strategies
				//TODO: Put in a method to avoid recursion and just create it once
				logger.warn("Table '" + tableName + "' was unexpectedly not found and gets recreated.");
				tableExists = false;
				this.save(event);
			} else {
				throw new SpRuntimeException(e.getMessage());
			}
		}
	}

	/**
	 * Executes the saved prepared statement {@code ps}. In case it is not initialized, it will call
	 * {@link JdbcClient#generatePreparedStatement(Map)} to prepare it
	 *
	 * @param event Data to be saved in the SQL table
	 * @throws SQLException When the statement cannot be executed
	 * @throws SpRuntimeException When the table name is not allowed or it is thrown
	 * by {@link SqlAttribute#setValue(Parameterinfo, Object, PreparedStatement)}
	 */
	private void executePreparedStatement(final Map<String, Object> event)
			throws SQLException, SpRuntimeException {
		if (ps != null) {
			ps.clearParameters();
		}
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			if (!parameters.containsKey(pair.getKey())) {
				//TODO: start the for loop all over again
				//TODO: Do it with eventSchema instead of the event
				generatePreparedStatement(event);
			}
			Parameterinfo p = parameters.get(pair.getKey());
			SqlAttribute.setValue(p, pair.getValue(), ps);
		}
		ps.executeUpdate();
	}

	/**
	 * Initializes the variables {@link JdbcClient#parameters} and {@link JdbcClient#ps}
	 * according to the parameter event.
	 *
	 * @param  event The event which is getting analyzed
	 * @throws SpRuntimeException When the tablename is not allowed
	 * @throws SQLException When the prepareStatment cannot be evaluated
	 */
	private void generatePreparedStatement(final Map<String, Object> event)
			throws SQLException, SpRuntimeException {
		// input: event
		// wanted: INSERT INTO test4321 ( randomString, randomValue ) VALUES ( ?,? );
		parameters.clear();
		StringBuilder statement1 = new StringBuilder("INSERT  INTO ");
		StringBuilder statement2 = new StringBuilder("VALUES ( ");
		checkRegEx(tableName, "Tablename");
		statement1.append(tableName).append(" ( ");

		// Starts at 1, since the parameterIndex in the PreparedStatement starts at 1 as well
		int i = 1;
		for (Map.Entry<String, Object> pair : event.entrySet()) {
		  checkRegEx(pair.getKey(), "Columnname");
			parameters.put(pair.getKey(),
					new Parameterinfo(i, SqlAttribute.getFromObject(pair.getValue())));
			statement1.append(pair.getKey()).append(", ");
			statement2.append("?,");
			i++;
		}
		statement1 = statement1.delete(statement1.length() - 2, statement1.length()).append(" ) ");
		statement2 = statement2.delete(statement2.length() - 1, statement2.length()).append(" );");
		ps = c.prepareStatement(statement1.append(statement2).toString());
	}

  /**
   * Creates a table with the name {@link JdbcClient#tableName} and the
   * properties {@link JdbcClient#eventProperties}
   *
   * @throws SpRuntimeException If the {@link JdbcClient#tableName}  is not allowed, if
   *    executeUpdate throws an SQLException or if {@link JdbcClient#extractEventProperties(List)}
   *    throws an exception
   */
	private void createTable() throws SpRuntimeException {
	  checkRegEx(tableName, "Tablename");

	  StringBuilder statement = new StringBuilder("CREATE TABLE \"");
	  statement.append(tableName).append("\" ( ");
	  statement.append(extractEventProperties(eventProperties)).append(" );");

    try {
      st.executeUpdate(statement.toString());
    } catch (SQLException e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  /**
   * Creates a SQL-Query with the given Properties (SQL-Injection save). Calls
   * {@link JdbcClient#extractEventProperties(List, String)} with an empty string
   *
   * @param properties The list of properties which should be included in the query
   * @return A StringBuilder with the query which needs to be executed in order to create the table
   * @throws SpRuntimeException See {@link JdbcClient#extractEventProperties(List, String)} for details
   */
	private StringBuilder extractEventProperties(List<EventProperty> properties)
      throws SpRuntimeException {
    return extractEventProperties(properties, "");
  }

  /**
   * Creates a SQL-Query with the given Properties (SQL-Injection save). For nested properties it
   * recursively extracts the information. EventPropertyList are getting converted to a string (so
   * in SQL to a VARCHAR(255). For each type it uses {@link SqlAttribute#getFromUri(String)}
   * internally to identify the SQL-type from the runtimeType.
   *
   * @param properties The list of properties which should be included in the query
   * @param preProperty A string which gets prepended to all property runtimeNames
   * @return A StringBuilder with the query which needs to be executed in order to create the table
   * @throws SpRuntimeException If the runtimeName of any property is not allowed
   */
	private StringBuilder extractEventProperties(List<EventProperty> properties, String preProperty)
      throws SpRuntimeException {
	  //TODO: test, if the string is empty and maybe throw an exception (if it is the bottom layer)
	  // output: "randomString VARCHAR(255), randomValue INT"
    StringBuilder s = new StringBuilder();
    String pre = "";
    for (EventProperty property : properties) {
      // Protection against SqlInjection

      checkRegEx(property.getRuntimeName(), "Column name");
      if (property instanceof EventPropertyNested) {
        // if it is a nested property, recursively extract the needed properties
        StringBuilder tmp = extractEventProperties(((EventPropertyNested)property).getEventProperties(),
            preProperty + property.getRuntimeName() + "_");
        if(tmp.length() > 0) {
          s.append(pre).append(tmp);
        }
      } else {
        // Adding the name of the property (e.g. "randomString")
        // Or for properties in a nested structure: input1_randomValue
        // "pre" is there for the ", " part
        s.append(pre).append("\"").append(preProperty).append(property.getRuntimeName()).append("\" ");

        // adding the type of the property (e.g. "VARCHAR(255)")
	      if (property instanceof EventPropertyPrimitive) {
          s.append(SqlAttribute.getFromUri(((EventPropertyPrimitive)property).getRuntimeType()));
        } else {
	        // Must be an EventPropertyList then
          s.append(SqlAttribute.getFromUri(XSD._string.toString()));
        }
      }
      pre = ", ";
    }

	  return s;
	}

  /**
   * Checks if the input string is allowed (regEx match and length > 0)
   *
   * @param input String which is getting matched with the regEx
   * @param as Information about the use of the input. Gets included in the exception message
   * @throws SpRuntimeException If {@code input} does not match with {@link JdbcClient#allowedRegEx}
   * or if the length of {@code input} is 0
   */
	private void checkRegEx(String input, String as) throws SpRuntimeException {
    if (!input.matches(allowedRegEx) || input.length() == 0) {
      throw new SpRuntimeException(as + " '" + input
          + "' not allowed (allowed: '" + allowedRegEx + "') with a min length of 1");
    }
  }

	private void validateTable() {
		throw new NotImplementedException();
	}

	/**
	 * Closes all open connections and statements of JDBC
	 */
	public void stop() throws SQLException {
		if(st != null) {
			st.close();
		}
		if(c != null) {
			c.close();
		}
		if(ps != null) {
			ps.close();
		}
	}
}
