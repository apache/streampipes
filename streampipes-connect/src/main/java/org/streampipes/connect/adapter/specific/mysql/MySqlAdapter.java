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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.streampipes.sdk.utils.Datatypes;

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
  private static List<Column> tableSchema = new ArrayList<>();
  private BinaryLogClient client;

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
    // Connect BinaryLogClient
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
      //TODO: Log exception rather than printStackTrace()
      e.printStackTrace();
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
      if (EventType.isUpdate(event.getHeader().getEventType())) {
        System.out.println("Data updated: ");
        for (Entry<Serializable[], Serializable[]> en : ((UpdateRowsEventData) event.getData()).getRows()) {
          sendChange(en.getValue());
        }
      } else if (EventType.isWrite(event.getHeader().getEventType())) {
        System.out.println("New data written: ");
        for (Serializable[] s : ((WriteRowsEventData) event.getData()).getRows()) {
          sendChange(s);
        }
      }
      stream = false;
    }
  }

  private void sendChange(Serializable[] rows) {
    Map<String, Object> out = new HashMap<>();
    for (int i = 0; i < rows.length; i++) {
      if (rows[i] != null) {
        out.put(tableSchema.get(i).getName(), rows[i]);
      } else {
        out.put(tableSchema.get(i).getName(), tableSchema.get(i).getDefaul());
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
    return null;
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    // Load JDBC Driver, connect JDBC Driver, Extract information, disconnect JDBC Driver
    EventSchema eventSchema = new EventSchema();
    GuessSchema guessSchema = new GuessSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    getConfigurations(adapterDescription);

    // Init JdbcDriver
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new AdapterException("MySql Driver not found.");
    }

    String server = "jdbc:mysql://" + host + ":" + port + "/";
    // Maybe do it with "ResultSet rs = c.getMetaData().getTables(null, null, tableName, null);"?
    String query = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE"
        + " TABLE_NAME = '" + table + "';";

    // Establish connection
    try (Connection connection = DriverManager.getConnection(server, user, pass);
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(query)) {

      while (resultSet.next()) {
        String name = resultSet.getString("COLUMN_NAME");
        String type = resultSet.getString("DATA_TYPE");

        String runtimename = "";
        Datatypes datatype;
        Object defaul;
        if (type.equals("bool") || type.equals("boolean")) {
          datatype = Datatypes.Boolean;
          defaul = Boolean.FALSE;
        } else if (type.equals("varchar") || type.equals("char")) {
          datatype = Datatypes.String;
          defaul = "";
        } else {
          datatype = Datatypes.Float;
          datatype = Datatypes.Integer;
          defaul = 0;
        }

        tableSchema.add(new Column(name, type, defaul));
        allProperties.add(PrimitivePropertyBuilder
            .create(datatype, runtimename)
            .build());
      }
    } catch (SQLException e) {
      throw new AdapterException("SqlException: " + e.getMessage()
          + ", Error code: " + e.getErrorCode()
          + ", SqlState: " + e.getSQLState());
    }
    // Also add timestmap

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
}
