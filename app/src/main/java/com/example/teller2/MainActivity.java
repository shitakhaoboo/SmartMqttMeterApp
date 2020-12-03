package com.example.teller2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView conn;
    private TextView conn1;
    private TextView conn2;
    private Button btn;
    public boolean onoff = true;
    boolean connectionFlag = false;
    public static String topic_send="Jkuat-grid/house1/status/change";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String clientId = "lakisama";
        final MqttAndroidClient client =new MqttAndroidClient(this.getApplicationContext(), "tcp://test.mosquitto.org:1883",clientId);
        conn = findViewById(R.id.conn_status);
        conn1 = findViewById(R.id.incoming);
        conn2 = findViewById(R.id.relay);
        btn = findViewById(R.id.button3);
        btn.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onoff)
                {
//            means the  relay is om and we're turning it off
                    sendMessage(client, topic_send,"off");

                }
                else
                {
                    sendMessage(client, topic_send,"on");
                }

            }
        });

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    conn.setText("CONNECTED");
                    connectionFlag = true;
                    btn.setEnabled(true);
                    final String topic1 = "Jkuat-grid/house1/balance";
                    final String topic2 = "Jkuat-grid/house1/status/now";
                    int qos1 = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic1, qos1);
                        IMqttToken subToken2 = client.subscribe(topic2, qos1);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards

                            }
                        });subToken2.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Toast.makeText(MainActivity.this, new String(message.getPayload()),Toast.LENGTH_SHORT).show();
                            if (topic.equals(topic1))
                            {
                                conn1.setText(new String(message.getPayload()));
                            }
                            else if(topic.equals(topic2))
                            {
                                String k = new String(message.getPayload());
                                if (k.equals("on"))
                                {
                                    onoff=true;
                                }
                                else if(k.equals("off"))
                                {
                                    onoff=false;
                                }
                                else
                                {
                                    onoff=false;
                                }
                                conn2.setText(new String(message.getPayload()));
                            }

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
                    // We are connected
                    //Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    // Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void GSecond(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);

    }public void GSecond2(View view){
        Intent intent2 = new Intent(this,chartActivity.class);
        startActivity(intent2);

    }
    void sendMessage(MqttAndroidClient client1,String topic, String msg) {
        String payload = msg;
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client1.publish(topic, message);
            Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}
