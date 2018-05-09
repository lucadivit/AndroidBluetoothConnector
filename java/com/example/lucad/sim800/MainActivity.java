package com.example.lucad.sim800;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity{
    private ManageBluetoothConnection bluetoothConnectionManager;
    private Context activityContext = MainActivity.this;
    private Button searchDevices;
    private Button sendData;
    private ListView devicesListView;
    private TextView dataInTextView;
    private ProgressBar progressBar;
    private List<String> namesOfFoundedDevices = new ArrayList<>();
    private List<String> nameOfConnectedDevice = new ArrayList<>();
    ArrayAdapter<String> devicesAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothConnectionManager.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Log.e("enble","attivato");
            initializeBroadcastReceiver();
        }else{
            System.exit(0);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "deviceFound":
                    String deviceName = intent.getExtras().getString("device");
                    namesOfFoundedDevices.add(deviceName);
                    displayDevices("founded");
                    break;
                case "deviceConnected":
                    progressBar.setVisibility(View.INVISIBLE);
                    displayDevices("onlyConnected");
                    Toast.makeText(context,context.getString(R.string.connected),Toast.LENGTH_SHORT).show();
                    break;
                case "deviceDisconnected":
                    Toast.makeText(context,R.string.disconnected,Toast.LENGTH_SHORT).show();
                    displayDevices("clear");
                case "discoveryFinished":
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                case "discoveryStarted":
                    progressBar.setVisibility(View.VISIBLE);
                    displayDevices("clear");
                    break;
                case "bluetoothTurnedOff":
                    System.exit(0);
                    break;
                case "CONNECTION_ERROR":
                    Log.e("MainActivity","connectionError");
                    Toast.makeText(context,context.getString(R.string.notConnected),Toast.LENGTH_SHORT).show();
                    break;
                case "SOCKET_READY":
                    Log.e("socket","SocketPronto");
                    bluetoothConnectionManager.readBluetoothData();
                    break;
                case "DATA_RECEIVED":
                    String dataReceived = intent.getExtras().getString("dataReceived");
                    new AlertDialog.Builder(context)
                            .setMessage(dataReceived)
                            .show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);//Rendo la progressBar invisibile
        searchDevices = findViewById(R.id.btnBluetooth);
        devicesListView = findViewById(R.id.devicesList);
        sendData = findViewById(R.id.sendCommand);
        dataInTextView = findViewById(R.id.ATCommand);
        devicesAdapter = new ArrayAdapter<>(activityContext, android.R.layout.simple_list_item_1);
        bluetoothConnectionManager = new ManageBluetoothConnection(activityContext);

        if (bluetoothConnectionManager.getBluetoothAdapter() != null){
            if (bluetoothConnectionManager.isEnabled() == Boolean.TRUE){
                initializeBroadcastReceiver();
            }else {
                Intent enableBT = bluetoothConnectionManager.enableBluetooth();
                startActivityForResult(enableBT, 1);//
                onActivityResult(1, -1, enableBT);
            }
            progressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.MULTIPLY);
        }else {
            System.exit(0);
        }

        searchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothConnectionManager.isConnected() == Boolean.TRUE){

                }else {
                    displayDevices("clear");
                    bluetoothConnectionManager.discoverBluetoothDevices();
                }
            }
        });

        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothConnectionManager.isConnected() == Boolean.TRUE){
                    bluetoothConnectionManager.writeBluetoothData(dataInTextView.getText().toString());
                    dataInTextView.setText("");
                }
                else {

                }
            }
        });

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (bluetoothConnectionManager.isConnected() == Boolean.TRUE){
                    progressBar.setVisibility(View.INVISIBLE);
                    bluetoothConnectionManager.cancelConnection();
                    displayDevices("clear");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    String deviceName = (String) adapterView.getAdapter().getItem(i);
                    displayDevices("clear");
                    String deviceAddress = bluetoothConnectionManager.getAddresByName(deviceName);
                    nameOfConnectedDevice.add(deviceName);
                    bluetoothConnectionManager.startBluetoothConnection(deviceAddress);
                }

            }
        });

    }

    public void initializeBroadcastReceiver(){
        registerReceiver(broadcastReceiver, new IntentFilter("deviceFound"));
        registerReceiver(broadcastReceiver, new IntentFilter("deviceConnected"));
        registerReceiver(broadcastReceiver, new IntentFilter("deviceDisonnected"));
        registerReceiver(broadcastReceiver, new IntentFilter("discoveryStarted"));
        registerReceiver(broadcastReceiver, new IntentFilter("discoveryFinished"));
        registerReceiver(broadcastReceiver, new IntentFilter("bluetoothTurnedOff"));
        registerReceiver(broadcastReceiver, new IntentFilter("DATA_RECEIVED"));
        registerReceiver(broadcastReceiver, new IntentFilter("CONNECTION_ERROR"));
        registerReceiver(broadcastReceiver, new IntentFilter("SOCKET_READY"));
    }

    public void displayDevices(String action){
        switch (action){
            case "founded":
                devicesAdapter.addAll(namesOfFoundedDevices);
                devicesAdapter.notifyDataSetChanged();
                devicesListView.setAdapter(devicesAdapter);
                break;
            case "onlyConnected":
                devicesAdapter.clear();
                devicesAdapter.notifyDataSetChanged();
                devicesListView.setAdapter(devicesAdapter);
                devicesAdapter.addAll(nameOfConnectedDevice);
                devicesAdapter.notifyDataSetChanged();
                devicesListView.setAdapter(devicesAdapter);
                nameOfConnectedDevice.clear();
                break;
            case "clear":
                namesOfFoundedDevices.clear();
                nameOfConnectedDevice.clear();
                devicesAdapter.clear();
                devicesAdapter.notifyDataSetChanged();
                devicesListView.setAdapter(devicesAdapter);
                break;
        }
    }
}
