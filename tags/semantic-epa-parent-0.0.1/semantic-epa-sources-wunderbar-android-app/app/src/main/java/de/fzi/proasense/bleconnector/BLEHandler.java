package de.fzi.proasense.bleconnector;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.relayr.android.RelayrSdk;
import io.relayr.android.ble.BleDevice;
import io.relayr.android.ble.RelayrBleSdk;
import io.relayr.android.ble.service.DirectConnectionService;
import io.relayr.java.ble.BleDeviceType;
import io.relayr.java.model.action.Reading;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by eberle on 15.02.2016.
 */
public class BLEHandler {
    private final static String TAG = BLEHandler.class.getName();

    private static BLEHandler instance;

    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private RelayrBleSdk relayrBleSdk;
    private List<Subscription> subs;
    private List<ConnectionInfo> dataSet;
    private List<BleDeviceType> bleDeviceTypes;
    private List<BleDevice> bleDevices;

    private BLEHandler(Context context, BluetoothAdapter mBluetoothAdapter, List<ConnectionInfo> dataSet) {
        this.context = context;
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.dataSet = dataSet;
        subs = new ArrayList<>();
        bleDevices = new ArrayList<>();
    }


    public static BLEHandler getInstance(Context context, BluetoothAdapter mBluetoothAdapter, List<ConnectionInfo> dataSet) {
        if (instance == null) {
            instance = new BLEHandler(context, mBluetoothAdapter, dataSet);
        }
        return instance;
    }

    public void setBleDeviceTypes(List<BleDeviceType> bleDeviceTypes) {
        this.bleDeviceTypes = bleDeviceTypes;
    }

    public List<BleDevice> getBleDevices() {
        return bleDevices;
    }

    public void start() {
        //nach BLE scannen:
        Log.i(TAG, "Start Connection BLE");
        Log.i(TAG, "BLEsupported:" + RelayrSdk.isBleSupported());
        Log.i(TAG, "BLEavailable:" + RelayrSdk.isBleAvailable());
        ((ActivityMain) context).connection(ConnectionVariable.BLE_DEVICE_UPDATE);
        relayrBleSdk = RelayrSdk.getRelayrBleSdk();

        Observable listObservable = relayrBleSdk.scan(bleDeviceTypes);
        subs.add(listObservable.subscribe(new FindBLEDevices()));
    }

    public boolean isConnected(){
        if(relayrBleSdk != null){
            return relayrBleSdk.isScanning();
        }
        return false;
    }

    public void stop(){
        for(Subscription sub : subs){
            sub.unsubscribe();
        }
    }

    public class FindBLEDevices implements Observer<List<BleDevice>> {

        @Override
        public void onCompleted() {
            Log.e(TAG, "complet");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(List<BleDevice> devices) {
            bleDevices = devices;
            for (BleDevice bleDevice : bleDevices) {
                Log.e(TAG, bleDevice.getAddress() + " # " + bleDevice.getName() + " # " + bleDevice.getMode());
                //subs.add(DirectConnectionService.connect(bleDevice, mBluetoothAdapter.getRemoteDevice(bleDevice.getAddress())).subscribe(new DirectBLEConnection()));
            }
            for (Subscription sub : subs) {
                sub.unsubscribe();
            }
            ((ActivityMain) context).connection(ConnectionVariable.BLE_DEVICE_UPDATE);
        }
    }

}
