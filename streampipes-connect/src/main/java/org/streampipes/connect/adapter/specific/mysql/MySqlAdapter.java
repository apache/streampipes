/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
 *
 */

package org.streampipes.connect.adapter.specific.mysql;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.streampipes.sdk.helpers.Labels;

public class MySqlAdapter extends SpecificDataStreamAdapter {

  public static final String ID = "http://streampipes.org/adapter/specific/mysql";

  private static String MYSQL_HOST = "MYSQL_HOST";
  private static String MYSQL_USER = "MYSQL_USER";
  private static String MYSQL_PASS = "MYSQL_PASS";
  private static String MYSQL_DB = "MYSQL_DB";
  private static String MYSQL_TABLE = "MYSQL_TABLE";
  private static String MYSQL_PORT = "MYSQL_PORT";

  private String host;
  private String user;
  private String pass;
  private String database;
  private String table;
  private String port;

  private boolean stream = false;
  private List<Column> tableSchema;
  private BinaryLogClient client;

  public MySqlAdapter() {
  }

  public MySqlAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);

    getConfigurations(adapterDescription);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    //TODO: Add Icon
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID,
        "MySql Adapter",
        "Connects to a MySql Database and sends out all inserted or updated rows. Needs"
            + "binary logging enabled (MySql command: \"SHOW VARIABLES LIKE 'log_bin';\") and a"
            + "user with sufficient privileges")
        //.iconUrl("ros.png")
        .requiredTextParameter(Labels.from(MYSQL_HOST, "Hostname", "Hostname of the MySql Server"))
        .requiredTextParameter(Labels.from(MYSQL_USER, "Username", "Username of the user"))
        .requiredTextParameter(Labels.from(MYSQL_PASS, "Password", "Password of the user"))
        .requiredTextParameter(Labels.from(MYSQL_DB, "Database", "Database in which the table is located"))
        .requiredTextParameter(Labels.from(MYSQL_TABLE, "Table", "Table which should be watched"))
        .requiredIntegerParameter(Labels.from(MYSQL_PORT, "Port", "Port of the MySql Server. Default: 3306"), 3306)
        .build();

    description.setAppId(ID);
    return  description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    checkJdbcDriver();
    extractTableInformation();
    // Connect BinaryLogClient
    //TODO: Add correct database to the hostname?
    client = new BinaryLogClient(host, Integer.parseInt(port), user, pass);
    EventDeserializer eventDeserializer = new EventDeserializer();
    eventDeserializer.setCompatibilityMode(
        EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
        EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
    );
    client.setEventDeserializer(eventDeserializer);
    client.registerEventListener(event -> sendEvent(event));
    try {
      client.connect();
    } catch (IOException e) {
      throw new AdapterException(e.getMessage());
    }
  }

  private void sendEvent(Event event) {
    // An event can contain multiple insertions/updates
    if (event.getHeader().getEventType() == EventType.TABLE_MAP) {
      // Check table and database, if the next event should be streamed
      if (((TableMapEventData) event.getData()).getDatabase().equals(database)
          && ((TableMapEventData) event.getData()).getTable().equals((table))) {
        stream = true;
      }
    }
    if (stream) {
      //TODO: remove System.out.println
      if (EventType.isUpdate(event.getHeader().getEventType())) {
        System.out.println("Data updated: ");
        for (Entry<Serializable[], Serializable[]> en : ((UpdateRowsEventData) event.getData()).getRows()) {
          sendChange(en.getValue());
        }
        stream = false;
      } else if (EventType.isWrite(event.getHeader().getEventType())) {
        for (Serializable[] s : ((WriteRowsEventData) event.getData()).getRows()) {
          sendChange(s);
        }
        stream = false;
      }
    }
  }

  private void sendChange(Serializable[] rows) {
    Map<String, Object> out = new HashMap<>();
    for (int i = 0; i < rows.length; i++) {
      if (rows[i] != null) {
        if (rows[i] instanceof byte[]) {
          // Strings are sent in byte arrays and have to be converted. TODO: Check that encoding is correct
          out.put(tableSchema.get(i).getName(), new String((byte[])rows[i]));
        } else {
          out.put(tableSchema.get(i).getName(), rows[i]);
        }
      } else {
        out.put(tableSchema.get(i).getName(), tableSchema.get(i).getDefault());
      }
    }
    adapterPipeline.process(out);
  }

  @Override
  public void stopAdapter() throws AdapterException {
    try {
      client.disconnect();
    } catch (IOException e) {
      throw new AdapterException("Thrown exception: " + e.getMessage());
    }
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new MySqlAdapter(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    // Load JDBC Driver, connect JDBC Driver, Extract information, disconnect JDBC Driver
    EventSchema eventSchema = new EventSchema();
    GuessSchema guessSchema = new GuessSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    getConfigurations(adapterDescription);

    checkJdbcDriver();
    extractTableInformation();

    for (Column column : tableSchema) {
      allProperties.add(PrimitivePropertyBuilder
          .create(column.getType(), database + "." + table + "." + column.getName())
          .label(column.getName())
          .build());
    }

    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);

    return guessSchema;
  }

  @Override
  public String getId() {
    return ID;
  }

  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
    for (StaticProperty sp : adapterDescription.getConfig()) {
      if (sp.getInternalName().equals(MYSQL_HOST)) {
        this.host= ((FreeTextStaticProperty) sp).getValue();
      } else if (sp.getInternalName().equals(MYSQL_USER)) {
        this.user = ((FreeTextStaticProperty) sp).getValue();
      } else if (sp.getInternalName().equals(MYSQL_PASS)) {
        this.pass = ((FreeTextStaticProperty) sp).getValue();
      } else if (sp.getInternalName().equals(MYSQL_DB)) {
        this.database = ((FreeTextStaticProperty) sp).getValue();
      } else if (sp.getInternalName().equals(MYSQL_TABLE)) {
        this.table = ((FreeTextStaticProperty) sp).getValue();
      } else {
        this.port = ((FreeTextStaticProperty) sp).getValue();
      }
    }
  }

  private void checkJdbcDriver() throws AdapterException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new AdapterException("MySql Driver not found.");
    }
  }

  private void extractTableInformation() throws AdapterException {
    String server = "jdbc:mysql://" + host + ":" + port + "/" + "?sslMode=DISABLED&allowPublicKeyRetrieval=true";
    ResultSet resultSet = null;
    tableSchema = new ArrayList<>();

    String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE FROM "
        + "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ? ORDER BY "
        + "ORDINAL_POSITION ASC;";

    try (Connection con = DriverManager.getConnection(server, user, pass);
        PreparedStatement statement = con.prepareStatement(query)) {

      statement.setString(1, table);
      statement.setString(2, database);
      resultSet = statement.executeQuery();

      if (resultSet.next()) {
        do {
          String name = resultSet.getString("COLUMN_NAME");
          String dataType = resultSet.getString("DATA_TYPE");
          String columnType = resultSet.getString("COLUMN_TYPE");
          tableSchema.add(new Column(name, dataType, columnType));
        } while(resultSet.next());
      } else {
        // No columns found -> Table/Database does not exist
        throw new IllegalArgumentException("Database/table not found");
      }
    } catch (SQLException e) {
      throw new AdapterException("SqlException: " + e.getMessage()
          + ", Error code: " + e.getErrorCode()
          + ", SqlState: " + e.getSQLState());
    } finally {
      try {
        resultSet.close();
      } catch (Exception e) {}
    }
  }
}
