package de.fzi.proasense.bleconnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
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

public class ActivityMain extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final String TAG = this.getClass().getCanonicalName();
    private BluetoothAdapter mBluetoothAdapter;
    private BLEHandler bleHandler;
    private Context context;
    private Set<BleDeviceType> bleDeviceTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_bleconnector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
// displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        bleHandler = BLEHandler.getInstance(context, mBluetoothAdapter);

        new RelayrSdk.Builder(this).build();
//        String clientId = MqttClient.generateClientId();
//        client =
//                new MqttAndroidClient(this.getApplicationContext(), "mqtt://ipe-koi04.fzi.de:1883",
//                        clientId);
//        options = new MqttConnectOptions();
//        options.setUserName("admin");
//        options.setPassword("admin".toCharArray());
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

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
            if (!MQTTHandler.getInstance(context).isConnected()) {
                try {
                    MQTTHandler.getInstance(context).connect();
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.i("TAG", "start");
                }
                bleHandler.start();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
