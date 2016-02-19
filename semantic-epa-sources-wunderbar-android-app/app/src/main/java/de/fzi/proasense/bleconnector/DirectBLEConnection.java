package de.fzi.proasense.bleconnector;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.relayr.android.ble.service.DirectConnectionService;
import io.relayr.java.model.action.Reading;
import rx.Observer;
import rx.Subscription;

/**
 * Created by eberle on 19.02.2016.
 */
public class DirectBLEConnection implements Observer<DirectConnectionService> {
    private final static String TAG = DirectBLEConnection.class.getName();

    private List<Subscription> subsDirectConnection;
    private Context context;

    public DirectBLEConnection(Context context) {
        subsDirectConnection = new ArrayList<>();
        this.context = context;
    }

    public void addSub(Subscription sub) {
        subsDirectConnection.add(sub);
    }

    @Override
    public void onCompleted() {
        for (Subscription sub : subsDirectConnection) {
            sub.unsubscribe();
        }
        Log.i(TAG, "complet");
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onNext(DirectConnectionService directConnectionService) {
        subsDirectConnection.add(directConnectionService.getReadings().subscribe(new ReadBLEValues(directConnectionService.getBleDevice().getAddress())));
    }


    public class ReadBLEValues implements Observer<Reading> {
        private String uuidSensor;
        private ConnectionInfo ci;

        public ReadBLEValues(String uuidSensor) {
            this.uuidSensor = uuidSensor;
            ci = new ConnectionInfo();
        }

        @Override
        public void onCompleted() {
            Log.i(TAG, "complet");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Reading reading) {
            ci.setAddress(uuidSensor);
            ci.setType(reading.meaning);
            ci.setTimestamp(System.currentTimeMillis() / 1000L);
            String msg = reading.value.toString();
            if (reading.meaning.equals("acceleration")
                    || reading.meaning.equals("angularSpeed")
                    || reading.meaning.equals("color")) {
                msg = msg.replace("=", ":")
                        .replace("x", "\"x\"")
                        .replace("y", "\"y\"")
                        .replace("z", "\"z\"")
                        .replace("blue", "\"blue\"")
                        .replace("green", "\"green\"")
                        .replace("red", "\"red\"")
                        .replace("{", "{\"timestamp\":" + (ci.getTimestamp()) + ",");
            } else {
                msg = "{" +
                        "\"timestamp\":" + (ci.getTimestamp()) + "," +
                        "\"" + reading.meaning + "\":" + reading.value.toString() + "}";
            }
            Log.i(TAG, reading.meaning + " meaning:" + reading.meaning + "-" + uuidSensor + " value:" + msg);
            MQTTHandler.getInstance(context).publish(uuidSensor + "/" + reading.meaning, msg);
        }
    }
}