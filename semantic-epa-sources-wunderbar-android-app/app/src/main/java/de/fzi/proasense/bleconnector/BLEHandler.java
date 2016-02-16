package de.fzi.proasense.bleconnector;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.relayr.android.RelayrSdk;
import io.relayr.android.ble.BleDevice;
import io.relayr.android.ble.RelayrBleSdk;
import io.relayr.android.ble.service.DirectConnectionService;
import io.relayr.java.ble.BleDeviceType;
import io.relayr.java.model.action.Reading;
import rx.Observable;
import rx.Observer;

/**
 * Created by eberle on 15.02.2016.
 */
public class BLEHandler {
    private final static String TAG = BLEHandler.class.getName();

    private static BLEHandler instance;

    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private RelayrBleSdk relayrBleSdk;

    private BLEHandler(Context context, BluetoothAdapter mBluetoothAdapter) {
        this.context = context;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }


    public static BLEHandler getInstance(Context context, BluetoothAdapter mBluetoothAdapter) {
        if (instance == null) {
            instance = new BLEHandler(context, mBluetoothAdapter);
        }
        return instance;
    }

    public void start() {
        //nach BLE scannen:
        Log.i(TAG, "Start Connection BLE");
        Log.i(TAG, "BLEsupported:" + RelayrSdk.isBleSupported());
        Log.i(TAG, "BLEavailable:" + RelayrSdk.isBleAvailable());
        List<BleDeviceType> bleDeviceTypes = new ArrayList<BleDeviceType>();

        relayrBleSdk = RelayrSdk.getRelayrBleSdk();
        if (relayrBleSdk.isScanning()) {
            //relayrBleSdk.stop();
        }
//        bleDeviceTypes.add(BleDeviceType.WunderbarHTU);
        bleDeviceTypes.add(BleDeviceType.WunderbarGYRO);
//        bleDeviceTypes.add(BleDeviceType.WunderbarLIGHT);
//        bleDeviceTypes.add(BleDeviceType.WunderbarMIC);
        Observable<List<BleDevice>> listObservable = relayrBleSdk.scan(bleDeviceTypes);
        listObservable.subscribe(new FindBLEDevices());
    }

    public void stop() {
        relayrBleSdk.stop();
    }

    public class FindBLEDevices implements Observer<List<BleDevice>> {

        @Override
        public void onCompleted() {
            Log.i(TAG, "complet");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(List<BleDevice> bleDevices) {
            for (BleDevice bleDevice : bleDevices) {
                DirectConnectionService.connect(bleDevice, mBluetoothAdapter.getRemoteDevice(bleDevice.getAddress())).subscribe(new DirectBLEConnection());
            }
        }
    }

    public class DirectBLEConnection implements Observer<DirectConnectionService> {

        @Override
        public void onCompleted() {
            Log.i(TAG, "complet");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(DirectConnectionService directConnectionService) {
            directConnectionService.getReadings().subscribe(new ReadBLEValues(directConnectionService.getBleDevice().getAddress()));
        }
    }

    public class ReadBLEValues implements Observer<Reading> {
        private String uuidSensor;

        public ReadBLEValues(String uuidSensor) {
            this.uuidSensor = uuidSensor;
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
                        .replace("red", "\"red\"");
//                        .replace("{","{\"timestamp\":"+(System.currentTimeMillis()/1000L)+",");
            } else {
                msg = "{" +
//                        "\"timestamp\":"+(System.currentTimeMillis()/1000L)+","+
                        "\"" + reading.meaning + "\":" + reading.value.toString() + "}";
            }
            Log.i(TAG, reading.meaning + " meaning:" + reading.meaning + "-" + uuidSensor + " value:" + msg);
            MQTTHandler.getInstance(context).publish(uuidSensor + "/" + reading.meaning, msg);
        }
    }
}
