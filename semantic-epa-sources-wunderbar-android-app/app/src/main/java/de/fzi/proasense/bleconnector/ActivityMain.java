package de.fzi.proasense.bleconnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.relayr.android.RelayrSdk;
import io.relayr.android.ble.BleDevice;
import io.relayr.android.ble.service.DirectConnectionService;
import io.relayr.java.ble.BleDeviceType;

public class ActivityMain extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final String TAG = this.getClass().getCanonicalName();
    private BluetoothAdapter mBluetoothAdapter;
    private BLEHandler bleHandler;
    private Context context;
    private List<ConnectionInfo> dataSet;
    private Map<String, CardView> sensorView;
    private Map<String, ConnectionDirectDeviceBLE> directConnection;
    private List<BleDeviceType> bleDeviceTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        dataSet = new ArrayList<>();
        sensorView = new HashMap<>();
        directConnection = new HashMap<>();
        bleDeviceTypes = new ArrayList<>();
        setContentView(R.layout.activity_bleconnector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Switch mqttConnection = (Switch) findViewById(R.id.mqttConnection);
        mqttConnection.setChecked(MQTTHandler.getInstance(context).isConnected());
        mqttConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MQTTHandler.getInstance(context).isConnected()) {
                    try {
                        MQTTHandler.getInstance(context).connect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    MQTTHandler.getInstance(context).disconnect(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        }
                    });
                }
                ((Switch)v).setChecked(MQTTHandler.getInstance(context).isConnected());
            }
        });


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        bleHandler = BLEHandler.getInstance(context, mBluetoothAdapter, dataSet);

        bleDeviceTypes.add(BleDeviceType.WunderbarHTU);
        bleDeviceTypes.add(BleDeviceType.WunderbarGYRO);
        bleDeviceTypes.add(BleDeviceType.WunderbarLIGHT);
        bleDeviceTypes.add(BleDeviceType.WunderbarMIC);
        bleHandler.setBleDeviceTypes(bleDeviceTypes);
        new RelayrSdk.Builder(this).build();

        final Switch bleConnection = (Switch) findViewById(R.id.bleConnection);
        bleConnection.setChecked(bleHandler.isConnected());
        bleConnection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BleDeviceType type = getBleDeviceTypeFromString(((Switch) v).getText().toString());
                if (bleHandler.isConnected()) {
                    stop();
                    Log.i(TAG,"stop");
                } else {
                    bleHandler.start();
                    Log.i(TAG, "start");
                }
                ((Switch)v).setChecked(bleHandler.isConnected());
            }
        });


        for (BleDeviceType bleDeviceType : bleDeviceTypes) {
            CardView v = (CardView) getLayoutInflater().inflate(R.layout.card_ble_connect_devices, null);
            ((ViewGroup) findViewById(R.id.bleConnections)).addView(v);
            sensorView.put(bleDeviceType.name(), v);
            TextView s = (TextView) v.findViewById(R.id.bleTypeConnect);
            s.setText(bleDeviceType.name());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bleconnector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            //TODO Datensatz löschen und neu laden lassen;
            long timestamp = System.currentTimeMillis() / 1000L;
            List<ConnectionInfo> newList = new ArrayList<>();
            for (ConnectionInfo ci : dataSet) {
                if (timestamp - ci.getTimestamp() < 5) {
                    newList.add(ci);
                }
            }
            dataSet = newList;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void connection(ConnectionVariable cv) {
        switch (cv) {
            case UNKNOWN:
                break;
            case MQTT_CONNECTION_LOST:
                Switch mqttConnection = (Switch) findViewById(R.id.mqttConnection);
                mqttConnection.setChecked(false);
                break;
            case BLE_DEVICE_UPDATE:
                for (Map.Entry<String, CardView> entry : sensorView.entrySet()) {
                    ((ViewGroup) entry.getValue().findViewById(R.id.bleDeviceList)).removeAllViews();
                }
                for(Map.Entry<String, ConnectionDirectDeviceBLE> entry : directConnection.entrySet()){
                    ConnectionDirectDeviceBLE cddBLE = entry.getValue();
                    if(cddBLE.isConnected())
                        cddBLE.getDirectBLEConnection().onCompleted();
                }
                directConnection = new HashMap<>();
                for (BleDevice bleDevice : bleHandler.getBleDevices()) {

                    ConnectionDirectDeviceBLE cddBLE = new ConnectionDirectDeviceBLE(bleDevice);
                    directConnection.put(bleDevice.getAddress(), cddBLE);
                    Switch s = (Switch) getLayoutInflater().inflate(R.layout.switch_ble_device, null);
                    ((ViewGroup) sensorView.get(bleDevice.getName()).findViewById(R.id.bleDeviceList)).addView(s);
                    s.setText(bleDevice.getAddress());
                    s.setChecked(cddBLE.isConnected());
                    s.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            ConnectionDirectDeviceBLE cddBLE = directConnection.get(((Switch) v).getText());
                            if (!cddBLE.isConnected()) {
                                DirectBLEConnection connection = new DirectBLEConnection(context);
                                connection.addSub(DirectConnectionService.connect(cddBLE.getBleDevice(), mBluetoothAdapter.getRemoteDevice(cddBLE.getBleDevice().getAddress())).subscribe(connection));
                                cddBLE.setDirectBLEConnection(connection);
                            } else {
                                cddBLE.getDirectBLEConnection().onCompleted();
                                cddBLE.setDirectBLEConnection(null);
                            }
                        }
                    });
                }
                break;
        }
    }

    private BleDeviceType getBleDeviceTypeFromString(String name) {
        for (BleDeviceType bleDeviceType : bleDeviceTypes) {
            if (bleDeviceType.name().equals(name)) {
                return bleDeviceType;
            }
        }
        return null;
    }

    private void stop(){
        for (Map.Entry<String, CardView> entry : sensorView.entrySet()) {
            ((ViewGroup) entry.getValue().findViewById(R.id.bleDeviceList)).removeAllViews();
        }
        for(Map.Entry<String, ConnectionDirectDeviceBLE> entry : directConnection.entrySet()){
            ConnectionDirectDeviceBLE cddBLE = entry.getValue();
            if(cddBLE.isConnected())
                cddBLE.getDirectBLEConnection().onCompleted();
        }
        directConnection = new HashMap<>();
        bleHandler.stop();
    }

    public class ConnectionDirectDeviceBLE {
        private BleDevice bleDevice;
        private DirectBLEConnection directBLEConnection;

        public ConnectionDirectDeviceBLE(BleDevice bleDevice) {
            this.bleDevice = bleDevice;
            this.directBLEConnection = directBLEConnection;
        }

        public BleDevice getBleDevice() {
            return bleDevice;
        }

        public void setBleDevice(BleDevice bleDevice) {
            this.bleDevice = bleDevice;
        }

        public DirectBLEConnection getDirectBLEConnection() {
            return directBLEConnection;
        }

        public void setDirectBLEConnection(DirectBLEConnection directBLEConnection) {
            this.directBLEConnection = directBLEConnection;
        }

        public boolean isConnected() {
            return directBLEConnection != null;
        }
    }

}
