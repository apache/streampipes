package de.fzi.cep.sepa.sources.samples.taxi;

import de.fzi.cep.sepa.sources.samples.adapter.AdapterSchemaTransformer;
import org.apache.commons.collections.map.HashedMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TaxiLineTransformer implements AdapterSchemaTransformer {

    @Override
    public Map<String, Object> transform(Object[] data, boolean withLabel) {
        Map<String, Object> result = new HashedMap();


        try {
            result.put("vendor_id" , Double.parseDouble((String) data[0]));
            result.put("tpep_pickup_datetime" , toTimestamp((String) data[1]));
            result.put("tpep_dropoff_datetime" , toTimestamp((String) data[2]));
            result.put("passenger_count" , Double.parseDouble((String) data[3]));
            result.put("trip_distance" , Double.parseDouble((String) data[4]));
            result.put("pickup_longitude" , Double.parseDouble((String) data[5]));
            result.put("pickup_latitude" , Double.parseDouble((String) data[6]));
            result.put("ratecode_id" , Double.parseDouble((String) data[7]));
////        result.put("store_and_fwd_flag" , Double.parseDouble((String) data[8]));
            result.put("dropoff_longitude" , Double.parseDouble((String) data[9]));
            result.put("dropoff_latitude" , Double.parseDouble((String) data[10]));
            result.put("payment_type" , Double.parseDouble((String) data[11]));
            result.put("fare_amount" , Double.parseDouble((String) data[12]));
            result.put("extra" , Double.parseDouble((String) data[13]));
            result.put("mta_tax" , Double.parseDouble((String) data[14]));
            result.put("tip_amount" , Double.parseDouble((String) data[15]));
            result.put("tolls_amount" , Double.parseDouble((String) data[16]));
            result.put("improvement_surcharge" , Double.parseDouble((String) data[17]));
            result.put("total_amount" , Double.parseDouble((String) data[18]));
            result.put("read_time" , System.currentTimeMillis());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    private long toTimestamp(String formattedDate) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(formattedDate);
        return date.getTime();
    }
}
