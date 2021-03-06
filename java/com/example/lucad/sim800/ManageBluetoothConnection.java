package com.example.lucad.sim800;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

public class ManageBluetoothConnection extends AppCompatActivity{
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private UUID uuid = null;
    private Boolean enableResult;
    private HashMap<String, String> devicesFounded = new HashMap<>();
    private Context context;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private byte[] buffer;
    private Handler handler = new Handler();

    ManageBluetoothConnection(Context context){
        setContext(context);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Log.e("Errore", "BluetoothAdapter Non Disponibile");
        }else {
            initializeBroadcastReceiver();
        }
    }

    private void initializeBroadcastReceiver(){
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter intentFilter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter intentFilter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter intentFilter4 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter intentFilter5 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        ((Activity) context).registerReceiver(broadcastReceiver, intentFilter);
        ((Activity) context).registerReceiver(broadcastReceiver, intentFilter1);
        ((Activity) context).registerReceiver(broadcastReceiver, intentFilter2);
        ((Activity) context).registerReceiver(broadcastReceiver, intentFilter3);
        ((Activity) context).registerReceiver(broadcastReceiver, intentFilter4);
        ((Activity) context).registerReceiver(broadcastReceiver, intentFilter5);
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUUID(String UUID){
        this.uuid = java.util.UUID.fromString(UUID);
    }

    public UUID getUUID(){
        return this.uuid;
    }

    public void generateRandomUUID(){
        setUUID(UUID.randomUUID().toString());
    }

    public String getAddresByName(String deviceName){
        return devicesFounded.get(deviceName);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public Boolean enableBluetooth(){
        if(!bluetoothAdapter.isEnabled()){
            int requestCode = 1;
            int resultCode = -1;
            Intent enableBT = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            onActivityResult(requestCode,resultCode, enableBT);
            ((Activity) context).startActivityForResult(enableBT,requestCode);
        }else {
            Log.e("Bluetooth","Bluetooth is already enable");
        }
        if (getEnableResult() == Boolean.TRUE){
            Log.d("Bluetooth","Bluetooth is activated with success");
            return Boolean.TRUE;
        }else {
            Log.e("Bluetooth","Bluetooth is not activated");
            return Boolean.FALSE;
        }
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public void discoverBluetoothDevices(){
        if(!bluetoothAdapter.isDiscovering()){

        }else{
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    public HashMap<String, String> getDevicesFounded() {
        return devicesFounded;
    }

    public void cancelDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    public void startBluetoothConnection(String deviceAddress){
        cancelDiscovery();
        final UUID uuidForConnection;
        if (getUUID() != null) {
            uuidForConnection = getUUID();
        }else {
            generateRandomUUID();
            uuidForConnection = getUUID();
        }
        bluetoothDevice = getBluetoothAdapter().getRemoteDevice(deviceAddress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket bluetoothSocketTemporary = null;
                OutputStream outputStreamTemporary = null;
                InputStream inputStreamTemporary = null;
                Class aClass = null;
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                Object[] params = new Object[] {Integer.valueOf(1)};
                Method method = null;
                try {
                    bluetoothSocketTemporary = bluetoothDevice.createRfcommSocketToServiceRecord(uuidForConnection);
                    aClass = bluetoothSocketTemporary.getRemoteDevice().getClass();
                } catch (IOException e) {
                    Log.e("Errore Di Connessione", e.toString());
                }

                try {
                    method = aClass.getMethod("createRfcommSocket", paramTypes);
                } catch (NoSuchMethodException e) {
                    Log.e("Errore Di Connessione", e.toString());
                }

                try {
                    bluetoothSocket = (BluetoothSocket) method.invoke(bluetoothSocketTemporary.getRemoteDevice(), params);
                } catch (IllegalAccessException e) {
                    Log.e("Errore Di Connessione", e.toString());
                } catch (InvocationTargetException e) {
                    Log.e("Errore Di Connessione", e.toString());
                }

                try {
                    bluetoothSocket.connect();
                    try {
                        inputStreamTemporary = bluetoothSocket.getInputStream();
                        Log.e("IS","inputstream Successo");
                    }catch (IOException e){
                        inputStreamTemporary = null;
                        Log.e("Errore","Errore in inputstream");
                    }
                    try {
                        outputStreamTemporary = bluetoothSocket.getOutputStream();
                        Log.e("OS","outputstream Successo");
                    }catch (IOException e){
                        outputStreamTemporary = null;
                        Log.e("Errore","Errore in outputstream");
                    }
                } catch (IOException e) {
                    Log.e("Errore", e.toString());
                }
                if (bluetoothSocket.isConnected() == Boolean.FALSE){
                    Log.e("Errore", "ErroreDiConnessione");
                    broadcastCustomIntent("CONNECTION_ERROR","connectionError","true");
                }else {
                    Log.e("OK", "Assegnazione I/O Stream con successo");
                    setInputStream(inputStreamTemporary);
                    setOutputStream(outputStreamTemporary);
                    broadcastCustomIntent("SOCKET_READY","socket","pronto");
                    if(getInputStream() == null || getOutputStream() == null){
                        try {
                            bluetoothSocket.close();
                            broadcastCustomIntent("CONNECTION_ERROR","connectionError","true");
                        } catch (IOException e) {
                            Log.e("ERRORE", "Qualcosa è andato storto. Riprovare");
                        }
                    }else {

                    }
                }
            }
        }).start();
    }

    public Boolean isConnected(){
        if (bluetoothSocket != null){
            return bluetoothSocket.isConnected();
        }else {
            return Boolean.FALSE;
        }
    }

    public void cancelConnection(){
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("Errore", e.toString());
        }
    }

    public Boolean getEnableResult() {
        return enableResult;
    }

    public void setEnableResult(Boolean enableResult) {
        this.enableResult = enableResult;
    }

    public void writeBluetoothData(String message) {
        try {
            getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            Log.e("Attenzione", "Error occurred when sending data", e);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void readBluetoothData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int numBytes;
                String message = "";
                String ch;
                Message msg;
                Intent i;
                int counter = 0;
                byte[] bytes = new byte[1024];
                setBuffer(bytes);
                while (true){
                    try {
                        numBytes = getInputStream().read(getBuffer());
                        msg = getHandler().obtainMessage(0, numBytes, -1, getBuffer());
                        ch = new String((byte[]) msg.obj, 0, msg.arg1);
                        message = message + ch;
                        if(message.endsWith("#") == Boolean.TRUE){
                            message = message.substring(0, message.length()-1);
                            broadcastCustomIntent("DATA_RECEIVED", "dataReceived", message);
                            message = "";
                        }else{

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Boolean isDiscovering(){
        return bluetoothAdapter.isDiscovering();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            setEnableResult(true);
        }else{
            setEnableResult(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        cancelConnection();
    }

    private void broadcastCustomIntent(String action, String name, String value){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(name,value);
        ((Activity) context).sendBroadcast(intent);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Intent i;
            String action = intent.getAction();
            final String ACTION_FOUND = BluetoothDevice.ACTION_FOUND;
            final String ACTION_ACL_CONNECTED = BluetoothDevice.ACTION_ACL_CONNECTED;
            final String ACTION_ACL_DISCONNECTED = BluetoothDevice.ACTION_ACL_DISCONNECTED;
            final String ACTION_DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
            final String ACTION_DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
            final String ACTION_STATE_CHANGED = BluetoothAdapter.ACTION_STATE_CHANGED;
            switch (action){
                case ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    getDevicesFounded().put(device.getName(), device.getAddress());
                    i = new Intent("deviceFound");
                    i.putExtra("device", device.getName());
                    context.sendBroadcast(i);
                    break;
                case ACTION_ACL_CONNECTED:
                    i = new Intent("deviceConnected");
                    i.putExtra("connected", true);
                    context.sendBroadcast(i);
                    break;
                case ACTION_DISCOVERY_STARTED:
                    i = new Intent("discoveryStarted");
                    i.putExtra("discStarted", true);
                    context.sendBroadcast(i);
                    break;
                case ACTION_DISCOVERY_FINISHED:
                    i = new Intent("discoveryFinished");
                    i.putExtra("discFinished", true);
                    context.sendBroadcast(i);
                case ACTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            i = new Intent("bluetoothTurnedOff");
                            i.putExtra("turnOff", true);
                            context.sendBroadcast(i);
                            break;
                    }
                    break;
                case ACTION_ACL_DISCONNECTED:
                    i = new Intent("deviceDisconnected");
                    i.putExtra("disconnected", true);
                    context.sendBroadcast(i);
                    break;
            }
        }
    };
}