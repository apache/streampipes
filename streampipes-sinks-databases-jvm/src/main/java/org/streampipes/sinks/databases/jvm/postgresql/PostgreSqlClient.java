package org.streampipes.sinks.databases.jvm.postgresql;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;


import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

public class PostgreSqlClient {
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
			throw new SpRuntimeException("Error: Hostname not allowed (...)");
		}
		if (!databaseName.matches("[a-zA-Z][a-zA-Z0-9]*")) {
			throw new SpRuntimeException("Error: Databasename not allowed (...)");
		}
	}

	public void connect() throws SpRuntimeException {
		try {
			String url = "jdbc:postgresql://" + postgreSqlHost + ":" + postgreSqlPort + "/" + databaseName;
			c = DriverManager.getConnection(url, user, password);
			st = c.createStatement();

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

	public void save(Map<String, Object> event, Logger LOG) throws SQLException {
		if (event == null) {
			System.out.println("Event is null");
			return;
		}
		String createQuery = "CREATE TABLE " + tableName + " ( ";
		// Final insert query = nameQuery + valueQuery
		String nameQuery = "INSERT INTO " + tableName + " ( ";
		String valueQuery = "VALUES ( ";
		for (Map.Entry<String, Object> pair : event.entrySet()) {
			// SQL-Injection-Test
			if (!tableExists) {
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
			}
			nameQuery += pair.getKey() + ", ";
			if (pair.getValue() instanceof String) {
				valueQuery += "'" + pair.getValue() + "',";
			} else {
				valueQuery += pair.getValue() + ", ";
			}
		}
		// Problems:
		// 1. SQL Injection. Solve with Prepared Statement
		// 2. Table looks different than the items to be saved

		createQuery = createQuery.substring(0, createQuery.length() - 2) + " );";
		nameQuery = nameQuery.substring(0, nameQuery.length() - 2) + " ) ";
		valueQuery = valueQuery.substring(0, valueQuery.length() - 2) + " );";
		String insertQuery = nameQuery + valueQuery;
		if(!tableExists) {
			// Creates the table (executeUpdate might throw a SQLException)
			try {
				st.executeUpdate(createQuery);
			} catch (SQLException e) {
				// If the exception says, that the table already exists, we can skip that and just insert the values.
				// For error codes see: https://www.postgresql.org/docs/current/errcodes-appendix.html
				//TODO: Really necessary?
				if(!e.getSQLState().equals("42P07")) {
					throw e;
				}
				LOG.warn("Variable tableExists was unexpectedly set to false, although the table exists.");
			}
			tableExists = true;
		}
		try {
			st.executeUpdate(insertQuery);
		} catch (SQLException e) {
			if (e.getSQLState().equals("42P01")) {
				// If the table does not exists (because it got deleted or something) we will try to create a new one. Otherwise we do not handle the exception
				//TODO: Possible problem of infinite recursion
				//TODO: Consider possible other exception handling strategies
				LOG.warn("Table " + tableName + " was unexpectedly not found and gets recreated.");
				tableExists = false;
				this.save(event, LOG);
			} else {
				throw e;
			}
		}
	}

	public void stop() throws SQLException {
		if(st != null) {
			st.close();
		}
		if(c != null) {
			c.close();
		}
	}
}
