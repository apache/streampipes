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

package org.streampipes.sinks.databases.jvm.postgresql;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PostgreSqlClient {
	// Allowed identifiers for PostgreSql
	// (https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)
	private static final String allowedRegEx = "^[a-zA-Z_][a-zA-Z0-9_]*$";

	private Integer postgreSqlPort;
	private String postgreSqlHost;
	private String databaseName;
	private String tableName;
	private String user;
	private String password;

	private boolean tableExists = false;

	private Logger logger = null;

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
		//TODO: 255? Or something else?
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
		public static SqlAttribute getFromObject(Object o) {
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

		private Parameterinfo(int index, SqlAttribute type) {
			this.index = index;
			this.type = type;
		}
	}


	PostgreSqlClient(String postgreSqlHost,
			Integer postgreSqlPort,
			String databaseName,
			String tableName,
			String user,
			String password,
			Logger logger) throws SpRuntimeException {
		this.postgreSqlHost = postgreSqlHost;
		this.postgreSqlPort = postgreSqlPort;
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.user = user;
		this.password = password;
		this.logger = logger;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new SpRuntimeException("PostgreSql Driver not found.");
		}
		validate();
		connect();
	}

	/**
	 * Checks whether the given {@link PostgreSqlClient#postgreSqlHost} and the
	 * {@link PostgreSqlClient#databaseName} are allowed
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
		//TODO: No Ipv6 support?
		if (!postgreSqlHost.matches(ipRegex) && !postgreSqlHost.matches(hostnameRegex)) {
			throw new SpRuntimeException("Error: Hostname '" + postgreSqlHost
					+ "' not allowed (allowed: '[a-zA-Z0-9./]*')");
		}
		if (!databaseName.matches(PostgreSqlClient.allowedRegEx)) {
			throw new SpRuntimeException("Error: Databasename '" + databaseName
					+ "' not allowed (allowed: '" + PostgreSqlClient.allowedRegEx + "')");
		}
	}

	/**
	 * Connects to the PostgreSql Server and initilizes {@link PostgreSqlClient#c} and
	 * {@link PostgreSqlClient#st}
	 *
	 * @throws SpRuntimeException When the connection could not be established (because of a
	 * wrong identification, missing database etc.)
	 */
	private void connect() throws SpRuntimeException {
		try {
			String u = "jdbc:postgresql://" + postgreSqlHost + ":" + postgreSqlPort + "/" + databaseName;
			c = DriverManager.getConnection(u, user, password);
			st = c.createStatement();

			//TODO: Remove this one?
			DatabaseMetaData md = c.getMetaData();
			ResultSet rs = md.getTables(null, null, tableName, null);
			if (rs.next()) {
				tableExists = true;
			}
			rs.close();

		} catch (SQLException e) {
			try {
				stop();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
			//TODO: printStackTrace() always needed?
			e.printStackTrace();
			//TODO: Or throw the original SQLException (also in the following cases)?
			throw new SpRuntimeException(e.getMessage());
		}
	}

	//TODO: How to specify the exception handling in the javadoc?
	/**
	 * Prepares a statement for the insertion of values or the
	 *
	 * @param event The event which should be saved to the Postgres table
	 * @throws SpRuntimeException When there was an error in the saving process
	 */
	void save(Map<String, Object> event) throws SpRuntimeException {
		if (event == null) {
			throw new SpRuntimeException("event is null");
		}
		if(!tableExists) {
			// Creates the table (executeUpdate might throw a SQLException)
			try {
				String createQuery = generateCreateStatement(event);
				st.executeUpdate(createQuery);
				tableExists = true;
			} catch (SQLException e) {
				throw new SpRuntimeException(e.getMessage());
			}
		}
		try {
			executePreparedStatement(event);
		} catch (SQLException e) {
			if (e.getSQLState().equals("42P01")) {
				// If the table does not exists (because it got deleted or something) we will try to create
				// a new one. Otherwise we do not handle the exception
				// For error codes see: https://www.postgresql.org/docs/current/errcodes-appendix.html
				//TODO: Possible problem of infinite recursion
				//TODO: Consider possible other exception handling strategies
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
	 * {@link PostgreSqlClient#generatePreparedStatement(Map)} to prepare it
	 *
	 * @param event Data to be saved in the SQL table
	 * @throws SQLException When the statement cannot be executed
	 * @throws SpRuntimeException When the table name is not allowed or it is thrown
	 * by {@link SqlAttribute#setValue(Parameterinfo, Object, PreparedStatement)}
	 */
	private void executePreparedStatement(Map<String, Object> event)
			throws SQLException, SpRuntimeException {
		if(ps != null) {
			ps.clearParameters();
		}
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			if(!parameters.containsKey(pair.getKey())) {
				//TODO: start the for loop all over again
				generatePreparedStatement(event);
			}
			Parameterinfo p = parameters.get(pair.getKey());
			SqlAttribute.setValue(p, pair.getValue(), ps);
		}
		ps.executeUpdate();
	}

	/**
	 * Initializes the variables {@link PostgreSqlClient#parameters} and {@link PostgreSqlClient#ps}
	 * according to the parameter event.
	 *
	 * @param  event The event which is getting analyzed
	 * @throws SpRuntimeException When the tablename is not allowed
	 * @throws SQLException When the prepareStatment cannot be evaluated
	 */
	private void generatePreparedStatement(Map<String, Object> event)
			throws SQLException, SpRuntimeException {
		// input: event
		// wanted: INSERT INTO test4321 ( randomString, randomValue ) VALUES ( ?,? );
		parameters.clear();
		StringBuilder statement1 = new StringBuilder("INSERT  INTO ");
		StringBuilder statement2 = new StringBuilder("VALUES ( ");
		if(!tableName.matches(PostgreSqlClient.allowedRegEx)) {
			throw new SpRuntimeException("Table name '" + tableName + "' not allowed (allowed: '"
					+ PostgreSqlClient.allowedRegEx + "')");
		}
		statement1.append(tableName).append(" ( ");

		// Starts at 1, since the parameterIndex in the PreparedStatement starts at 1 as well
		int i = 1;
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			if(!pair.getKey().matches(PostgreSqlClient.allowedRegEx)) {
				throw new SpRuntimeException("Column name '" + pair.getKey() + "' not allowed (allowed: '"
            + PostgreSqlClient.allowedRegEx + "')");
			}
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
	 * Prepares a statement for the creation of a table matched to the event
	 * (takes care of SQL Injection)
	 *
	 * @param  event  an absolute URL giving the base location of the image
	 * @return      an SQL-Injection save statement, which then can be used in a JDBC-statement
	 * @throws SpRuntimeException if the event has illegal names for the parameters or if the table
	 * name is not allowed
	 */
	private String generateCreateStatement(Map<String, Object> event) throws SpRuntimeException {
		// input: event(), tablename
		// output (example): "CREATE TABLE test4321 ( randomString VARCHAR(255), randomValue INT );"
		StringBuilder statement = new StringBuilder("CREATE TABLE ");

		if(!tableName.matches(PostgreSqlClient.allowedRegEx)) {
			throw new SpRuntimeException("Table name '" + tableName + "' not allowed (allowed: '"
					+ PostgreSqlClient.allowedRegEx + "')");
		}
		statement.append(tableName).append(" ( ");

		for (Map.Entry<String, Object> pair : event.entrySet()) {
			if(!pair.getKey().matches(PostgreSqlClient.allowedRegEx)) {
				throw new SpRuntimeException("Column name '" + pair.getKey() + "' not allowed (allowed: '"
						+ PostgreSqlClient.allowedRegEx + "')");
			}
			statement.append(pair.getKey()).append(" ").append(
					SqlAttribute.getFromObject(pair.getValue())).append(", ");

		}
		return statement.delete(statement.length() - 2, statement.length()).append(" );").toString();
	}

	/**
	 * Closes all open connections and statements of JDBC
	 */
	void stop() throws SQLException {
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
