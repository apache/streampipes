
package org.apache.streampipes.dataexplorer.commons;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DataExplorerWriter {
    private InfluxDB influxDB;

    // TODO return a connection here
    public void connect(DataExplorerConnectionSettings dataExplorerConnectionSettings) {
        this.influxDB = InfluxDBFactory.connect(dataExplorerConnectionSettings.getInfluxDbHost(),
                dataExplorerConnectionSettings.getUser(), dataExplorerConnectionSettings.getPassword());
    }

    public void close() {
        this.influxDB.close();
    }

    public void write(Map<String, Object> data,
                      String measurement) {
        Point.Builder builder = Point.measurement(measurement)
                .time((Long) data.get("timestamp"), TimeUnit.MILLISECONDS);

        data.remove("timestamp");

        for (String key : data.keySet()) {
            if (data.get(key) instanceof Double || data.get(key) == null) {
                builder.addField(key, (Double) data.get(key));
            } else if (data.get(key) instanceof Integer) {
                builder.addField(key, (Integer) data.get(key));
            } else {
                builder.addField(key, (String) data.get(key));
            }
        }

        this.influxDB.setDatabase(DataExplorerDefaults.DATA_LAKE_DATABASE_NAME);
        this.influxDB.write(builder.build());
    }

}
