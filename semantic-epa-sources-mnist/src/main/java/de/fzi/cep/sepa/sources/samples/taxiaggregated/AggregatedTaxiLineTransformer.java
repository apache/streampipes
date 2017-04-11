package de.fzi.cep.sepa.sources.samples.taxiaggregated;

import de.fzi.cep.sepa.flink.samples.count.aggregate.CountAggregateConstants;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sources.samples.adapter.AdapterSchemaTransformer;
import org.apache.commons.collections.map.HashedMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AggregatedTaxiLineTransformer implements AdapterSchemaTransformer {

    @Override
    public Map<String, Object> transform(Object[] data) {
        Map<String, Object> result = new HashedMap();


        result.put(CountAggregateConstants.AGGREGATE_TAXI_COUNT, Integer.parseInt((String) data[25]));
        result.put(CountAggregateConstants.WINDOW_TIME_START, Long.parseLong((String) data[21]));
        result.put(CountAggregateConstants.WINDOW_TIME_END, Long.parseLong((String) data[5]));
        result.put(CountAggregateConstants.PASSENGER_COUNT_AVG, Integer.parseInt((String) data[30]));
        result.put(CountAggregateConstants.TRIP_DISTANCE_AVG, Integer.parseInt((String) data[4]));
        result.put(CountAggregateConstants.EXTRA_AVG, Integer.parseInt((String) data[2]));
        result.put(CountAggregateConstants.TIP_AMOUNT_AVG, Integer.parseInt((String) data[11]));
        result.put(CountAggregateConstants.TOLLS_AMOUNT_AVG, Integer.parseInt((String) data[9]));
        result.put(CountAggregateConstants.FARE_AMOUNT_AVG, Integer.parseInt((String) data[1]));
        result.put(CountAggregateConstants.TOTAL_AMOUNT_AVG, Integer.parseInt((String) data[0]));
        result.put(CountAggregateConstants.RATE_CODE_ID_1, Integer.parseInt((String) data[19]));
        result.put(CountAggregateConstants.RATE_CODE_ID_2, Integer.parseInt((String) data[18]));
        result.put(CountAggregateConstants.RATE_CODE_ID_3, Integer.parseInt((String) data[22]));
        result.put(CountAggregateConstants.RATE_CODE_ID_4, Integer.parseInt((String) data[20]));
        result.put(CountAggregateConstants.RATE_CODE_ID_5, Integer.parseInt((String) data[24]));
        result.put(CountAggregateConstants.RATE_CODE_ID_6, Integer.parseInt((String) data[23]));
        result.put(CountAggregateConstants.PAYMENT_TYPE_1, Integer.parseInt((String) data[16]));
        result.put(CountAggregateConstants.PAYMENT_TYPE_2, Integer.parseInt((String) data[14]));
        result.put(CountAggregateConstants.PAYMENT_TYPE_3, Integer.parseInt((String) data[15]));
        result.put(CountAggregateConstants.PAYMENT_TYPE_4, Integer.parseInt((String) data[12]));
        result.put(CountAggregateConstants.PAYMENT_TYPE_5, Integer.parseInt((String) data[13]));
        result.put(CountAggregateConstants.PAYMENT_TYPE_6, Integer.parseInt((String) data[10]));
        result.put(CountAggregateConstants.MTA_TAX, Integer.parseInt((String) data[29]));
        result.put(CountAggregateConstants.IMPROVEMENT_SURCHARGE, Integer.parseInt((String) data[17]));
        result.put(CountAggregateConstants.GRID_LAT_NW_KEY, Double.parseDouble((String) data[7]));
        result.put(CountAggregateConstants.GRID_LON_NW_KEY, Double.parseDouble((String) data[8]));
        result.put(CountAggregateConstants.GRID_LAT_SE_KEY, Double.parseDouble((String) data[3]));
        result.put(CountAggregateConstants.GRID_LON_SE_KEY,Double.parseDouble((String) data[28]));
        result.put(CountAggregateConstants.GRID_CELL_ID, (String) data[6]);
        result.put("delay_label", (String) data[26]);


        return result;
    }
}
