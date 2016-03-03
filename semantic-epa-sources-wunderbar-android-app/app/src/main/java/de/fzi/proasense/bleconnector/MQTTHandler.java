package de.fzi.proasense.bleconnector;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by eberle on 15.02.2016.
 */
public class MQTTHandler implements MqttCallback {
    private static MQTTHandler instance;
    private static String HOST = "ipe-koi04.fzi.de";
    private static String PORT = "1883";
    private static String DEVICE_ID = "Smartphone";
    private static String USERNAME = "admin";
    private static String PASSWORD = "admin";
    private static String TOPIC = "wunderbar";
    private final String TAG = this.getClass().getCanonicalName();
    private MqttClient mqttClient;
    private Context context;

    private MQTTHandler(Context context) {
        this.context = context;
        DEVICE_ID += ":" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static MQTTHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MQTTHandler(context);
        }
        return instance;
    }


    @Override
    public void connectionLost(Throwable throwable) {
        ((ActivityMain)context).connection(ConnectionVariable.MQTT_CONNECTION_LOST);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.i("TAG", "messag");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.i("TAG", "delivery");
    }

    public void connect() throws MqttException {
        if (!isConnected()) {
            String connectionUri = "tcp://" + HOST + ":" + PORT;

            if (mqttClient != null) {
                //mqttClient.unregisterResources();
                mqttClient = null;
            }
            MemoryPersistence memoryPersistence = new MemoryPersistence();
            mqttClient = new MqttClient(connectionUri, DEVICE_ID, memoryPersistence);
            mqttClient.setCallback(this);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            try {
                mqttClient.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        if (isConnected()) {
            Log.i(TAG, "connected");
//            mqttClient.subscribe(TOPIC);
        }
    }

    public void disconnect(IMqttActionListener listener) {
        if (isConnected()) {
            try {
                mqttClient.disconnect();
                mqttClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String topic, String msg) {
        if (isConnected()) {
            MqttMessage mqttMsg = new MqttMessage(msg.getBytes());
            mqttMsg.setRetained(false);
            mqttMsg.setQos(0);
            try {
                mqttClient.publish(TOPIC + "/" + topic, mqttMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        if (mqttClient != null) {
            return mqttClient.isConnected();
        }
        return false;
    }
}
