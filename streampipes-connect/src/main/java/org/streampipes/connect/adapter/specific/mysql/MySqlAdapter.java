package org.streampipes.connect.adapter.specific.mysql;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.specific.SpecificDataStreamAdapter;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.streampipes.model.connect.guess.GuessSchema;
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
        .requiredIntegerParameter(Labels.from(MYSQL_PORT, "Port", "Port of the MySql Server"), 3306)
        .build();

    description.setAppId(ID);
    return  description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    // Connect BinaryLogClient
  }

  @Override
  public void stopAdapter() throws AdapterException {
    // Disconnect BinaryLogClient
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return null;
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {
    // Load JDBC Driver, connect JDBC Driver, Extract information, disconnect JDBC Driver
    return null;
  }

  @Override
  public String getId() {
    return ID;
  }
}
