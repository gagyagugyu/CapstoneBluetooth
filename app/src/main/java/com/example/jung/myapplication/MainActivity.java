package com.example.jung.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    public final static String TAG="main";
    public final static int REQUEST_ENABLE_BT = 1;
    public final static int REQUEST_CONNECT_DEVICE = 2;
    private BluetoothConnectService mConectService = null;
    private final Handler mHandler = new Handler();
    Button connectBluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConectService = new BluetoothConnectService(this,mHandler);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            Log.d(TAG,"Do not support bluetooth device");
        }
        if(mBluetoothAdapter.isEnabled()==false){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectBluetooth = (Button)findViewById(R.id.connectButton);
        connectBluetooth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                scanDevice();
            }
        });
    }
    public void scanDevice() {
        Log.d(TAG, "Scan Device");
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);


        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode== Activity.RESULT_OK){
                    try{

                        connectDevice(data);
                    }
                    catch(java.lang.NullPointerException e){
                        Log.d(TAG,"What the hell are you doing?");
                        connectDevice(data);

                    }

                }
                break;
            case REQUEST_ENABLE_BT:
                if(resultCode== Activity.RESULT_OK){
                    Log.d(TAG,"Bluetooth Activate");

                    connectDevice(data);
                }
                else{
                    Log.d(TAG,"Bluetooth not support");
                }
                break;
        }
    }
    protected void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device

        mConectService.connect(device);
    }
}
