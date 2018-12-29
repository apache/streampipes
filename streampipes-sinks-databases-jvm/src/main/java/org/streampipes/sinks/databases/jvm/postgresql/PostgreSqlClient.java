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
import java.util.Map;

public class PostgreSqlClient {
	// Allowed identifieres for PostgreSql (https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)
	private static final String allowedRegEx = "^[a-zA-Z_][a-zA-Z0-9_]*$";

	private Integer postgreSqlPort;
	private String postgreSqlHost;
	private String databaseName;
	private String tableName;
	private String user;
	private String password;

	boolean tableExists = false;

	// Needed for the connection
	Connection c = null;
	Statement  st = null;
	PreparedStatement ps = null;

	public PostgreSqlClient(String postgreSqlHost, Integer postgreSqlPort, String databaseName, String tableName, String user, String password) throws SpRuntimeException {
		this.postgreSqlHost = postgreSqlHost;
		this.postgreSqlPort = postgreSqlPort;
		this.databaseName = databaseName;
		this.tableName = tableName;
		this.user = user;
		this.password = password;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		validate();
		connect();
	}

	public String getPostgreSqlHost() {
		return postgreSqlHost;
	}

	public void setPostgreSqlHost(String postgreSqlHost) {
		this.postgreSqlHost = postgreSqlHost;
	}

	public void setPostgreSqlPort(Integer postgreSqlPort) {
		this.postgreSqlPort = postgreSqlPort;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void validate() throws SpRuntimeException {
		// Validates the database name and the attributes
		if (!postgreSqlHost.matches("[a-zA-Z0-9./]*")) {
			//TODO: How can the hostname look like?
			throw new SpRuntimeException("Error: Hostname '" + postgreSqlHost + "' not allowed (allowed: '[a-zA-Z0-9./]*)'");
		}
		if (!databaseName.matches(PostgreSqlClient.allowedRegEx)) {
			throw new SpRuntimeException("Error: Databasename '" + databaseName + "' not allowed (allowed: '" + PostgreSqlClient.allowedRegEx + "')");
		}
	}

	public void connect() throws SpRuntimeException {
		try {
			String url = "jdbc:postgresql://" + postgreSqlHost + ":" + postgreSqlPort + "/" + databaseName;
			c = DriverManager.getConnection(url, user, password);
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
			throw new SpRuntimeException(e.getMessage());
		}
	}

	//TODO: How to specify the exception handling in the javadoc?
	/**
	 * Prepares a statement for the insertion of values or the
	 *
	 * @param  event  the event which should be saved to the Postgres table
	 * @param  LOG the logger used in this pipeline
	 * @throws SQLException In case the "executeUpdate()" method throws an unhandled exception. Handled exception codes
	 * 			(e.getSQLState) are '42P07' and '42P01'
	 */
	public void save(Map<String, Object> event, Logger LOG) throws SQLException {
		if (event == null) {
			System.out.println("Event is null");
			return;
		}
		// Final insert query = nameQuery + valueQuery
		String qu = "INSERT INTO (";
		String nameQuery = "INSERT INTO " + tableName + " ( ";
		String valueQuery = "VALUES ( ";
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			nameQuery += pair.getKey() + ", ";
			if (pair.getValue() instanceof String) {
				valueQuery += "'" + pair.getValue() + "',";
			} else {
				valueQuery += pair.getValue() + ", ";
			}
		}
		// Problems:
		// 1. SQL Injection. Solve with prepared statement
		// Problem with prepared statements: Dynamic column names (https://stackoverflow.com/questions/3135973/variable-column-names-using-prepared-statements)
		// Possible Solutions:
		// - check the column names with a regex and the rest with prepared statements (then needs to extract the variable type every time)
		// - check everything with a regex
		// - in the first run, whitelist the column names (e.g. in a set) and prepare a statement for the input
		// 		which is used everytime. As soon as the event changes, we call the method again and

		//createQuery = createQuery.substring(0, createQuery.length() - 2) + " );";
		nameQuery = nameQuery.substring(0, nameQuery.length() - 2) + " ) ";
		valueQuery = valueQuery.substring(0, valueQuery.length() - 2) + " );";

		String insertQuery = nameQuery + valueQuery;
		if(!tableExists) {
			// Creates the table (executeUpdate might throw a SQLException)
			try {
				String createQuery = prepareCreateStatement(event, tableName);
				st.executeUpdate(createQuery);
			} catch (SQLException e) {
				// If the exception says, that the table already exists, we can skip that and just insert the values.
				// For error codes see: https://www.postgresql.org/docs/current/errcodes-appendix.html
				//TODO: Really necessary?
				if(!e.getSQLState().equals("42P07")) {
					throw e;
				}
				LOG.warn("Variable tableExists was unexpectedly set to false, although the table exists.");
			} catch (SpRuntimeException e) {
				LOG.error("");
				e.printStackTrace();
			}
			tableExists = true;
		}
		try {
			executePreparedStatement(event);
			st.executeUpdate(insertQuery);
		} catch (SQLException e) {
			if (e.getSQLState().equals("42P01")) {
				// If the table does not exists (because it got deleted or something) we will try to create a new one. Otherwise we do not handle the exception
				//TODO: Possible problem of infinite recursion
				//TODO: Consider possible other exception handling strategies
				LOG.warn("Table '" + tableName + "' was unexpectedly not found and gets recreated.");
				tableExists = false;
				this.save(event, LOG);
			} else {
				throw e;
			}
		}
	}

	private void executePreparedStatement(Map<String, Object> event) {
		// Dont do that. Init the map in the constructor empty and then for the first event the first entry will not
		// be found (so it will call createPrepatedStatement(event) anyways(
		if(ps == null) {
			createPreparedStatement(event);
		}
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			// check the map, if the key is in there (string -> id) and what type it is
			// if it is, do "ps.setString(i, pair->value);"
			// otherwise call createPreparedStatement()
			// Then execute it
		}
	}

	private void createPreparedStatement(Map<String, Object> event) {
		// input: event
		// output: INSERT INTO test4321 ( randomString, randomValue, count, timestamp ) VALUES ( ?,?,?,? );

		String statement1 = "INSERT  INTO ";
		String statement2 = "VALUES ";
	}

	/**
	 * Tries to identify the Datatype of the String and returns the needed SQL-Datatype.
	 * Can identify Integer, Long, Float and Doubles. In all other cases it returns a VARCHAR(255)
	 *
	 * @param  o  an Object from the event which type needs to be identified
	 * @return a string with the corresponding SQL-Datatype
	 */
	private static String getDataType(Object o) {
		//TODO: What other types are possible?
		String r;
		if (o instanceof Integer) {
			r = "INT";
		} else if (o instanceof Long) {
			r = "BIGINT";
		} else if (o instanceof Float) {
			r = "FLOAT";
		} else if (o instanceof Double) {
			r = "DOUBLE";
		} else {
			//TODO: 255? Or something else?
			r = "VARCHAR(255)";
		}
		return r;
	}

	/**
	 * Prepares a statement for the creation of a table matched to the event (takes care of SQL Injection)
	 *
	 * @param  event  an absolute URL giving the base location of the image
	 * @param  tableName the name of the sql table which should be created
	 * @return      an SQL-Injection save statement, which then can be used in a JDBC-statement
	 */
	private static String prepareCreateStatement(Map<String, Object> event, String tableName) throws SpRuntimeException {
		// input: event(), tablename
		// output (example): "CREATE TABLE test4321 ( randomString VARCHAR(255), randomValue INT, count INT, timestamp BIGINT );"
		String statement = "CREATE TABLE ";

		if(!tableName.matches(PostgreSqlClient.allowedRegEx)) {
			throw new SpRuntimeException("Table name '" + tableName + "' not allowed (allowed: '" + PostgreSqlClient.allowedRegEx + "')");
		}
		statement += tableName + " ( ";

		for (Map.Entry<String, Object> pair : event.entrySet()) {
			if(!pair.getKey().matches(PostgreSqlClient.allowedRegEx)) {
				throw new SpRuntimeException("Column name '" + pair.getKey() + "' not allowed (allowed: '" + PostgreSqlClient.allowedRegEx + "')");
			}
			statement += pair.getKey() + " " + PostgreSqlClient.getDataType(pair.getValue()) + ", ";
		}
		return statement.substring(0, statement.length() - 2) + " );";
	}

	// needed: mapping key -> position in the statement

	/**
	 * Prepares a statement for the insertion of values or the
	 *
	 * @param  event  an absolute URL giving the base location of the image
	 * @param  tableName the name of the sql table
	 * @return      an SQL-Injection save statement, which then can be used in a prepared statement
	 */
	public String prepareInsertStatement(Map<String, Object> event, String tableName) {
		// input: event()
		// output: INSERT INTO test4321 ( randomString, randomValue, count, timestamp ) VALUES ( ?,?,?,? );
		String nameQuery = "";
		String valueQuery = "";
		String createQuery = "";
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			createQuery += pair.getKey();
			//TODO: What other types are possible?
			if (pair.getValue() instanceof Integer) {
				createQuery += " INT, ";
			} else if (pair.getValue() instanceof Long) {
				createQuery += " BIGINT, ";
			} else if (pair.getValue() instanceof Float) {
				createQuery += " FLOAT, ";
			} else if (pair.getValue() instanceof Double) {
				createQuery += " DOUBLE, ";
			} else {
				//TODO: 255? Or something else?
				createQuery += " VARCHAR(255), ";
			}
			nameQuery += pair.getKey() + ", ";
			if (pair.getValue() instanceof String) {
				valueQuery += "'" + pair.getValue() + "',";
			} else {
				valueQuery += pair.getValue() + ", ";
			}
		}
		String re = "";
		return re;
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
