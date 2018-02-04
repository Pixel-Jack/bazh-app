package fr.clement.rennsurrection.bluesound.ThreadChat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import fr.clement.rennsurrection.bluesound.Main.MainActivity;

/**
 * Created by Clément on 06/02/2017.
 * This code can launch a server connection with the bluetoothDevice given.
 * Warning : a temporary Uuid is fixed
 * Ce code permet de me connecter au serveur mis en place par le bluetoothDevice donné en paramètre
 * Attention : on a fixé ici un Uuid temporaire cette option reste à améliorer
 */
public class ConnectThread extends Thread {
    private final static String ACTION_STATE_CHAT_CHANGED = "fr.clement.rennsurrection.testbluetooth.ACTION_STATE_CHAT_CHANGED";
    private final static String EXTRA_STATE_CHAT = "fr.clement.rennsurrection.testbluetooth.EXTRA_STATE_CHAT";
    private  final static  int STATE_CHAT_CONNECTED = MainActivity.STATE_CHAT_CONNECTED;
    private final static int STATE_CHAT_DECONNECTED = MainActivity.STATE_CHAT_DISCONNECTED;

    private BluetoothSocket mmSocket = null;
    private BluetoothDevice mmDevice = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private Handler handlerReception = null;
    private EmissionThread emissionThread = null;
    private ReceptionThread receptionThread = null;

    private InputStream is = null;
    private OutputStream os = null;

    private Thread emiThread = null;
    private Thread recThread = null;

    private final static int MESSAGE = 0;

    private Context context = null;

    // Intent to communicate with MainActivity
    private Intent intentCom = null;

    public ConnectThread(BluetoothAdapter btAdapter, BluetoothDevice device, Handler handlerReception, Context econtext){
        BluetoothSocket tmp = null;
        mmDevice = device; //device
        UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            tmp = device.createRfcommSocketToServiceRecord(myUuid);
        } catch (IOException e) { }
        mmSocket = tmp;
        this.mBluetoothAdapter = btAdapter;
        this.handlerReception = handlerReception;
        context = econtext;
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try {
            mmDevice.createBond();
            mmSocket.connect();
            intentCom = new Intent(ACTION_STATE_CHAT_CHANGED);
            intentCom.putExtra(EXTRA_STATE_CHAT, STATE_CHAT_CONNECTED);
            context.sendBroadcast(intentCom);
            is = mmSocket.getInputStream();
            os = mmSocket.getOutputStream();
            emissionThread = new EmissionThread(os, context);
            emiThread = new Thread(emissionThread);
            emiThread.start();
            receptionThread = new ReceptionThread(is, handlerReception, context);
            recThread = new Thread(receptionThread);
            recThread.start();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
                intentCom = new Intent(ACTION_STATE_CHAT_CHANGED);
                intentCom.putExtra(EXTRA_STATE_CHAT, STATE_CHAT_DECONNECTED);
                context.sendBroadcast(intentCom);
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
            return;
        }
    }

    public void cancel() {
        try {
            if(receptionThread != null){
                receptionThread.cancel();
            }
            if(emissionThread != null){
                emissionThread.cancel();
            }
            if(mmSocket != null){
                mmSocket.close();
            }
            if(recThread != null){
                recThread.interrupt();
            }
            if(emiThread != null){
                emiThread.interrupt();
            }
            if(is != null){
                is.close();
            }
            if(os != null){
                os.close();
            }
        } catch (IOException e) { }
    }

    public void sendMessage(String message){
        if(emissionThread != null){
            emissionThread.sendMessage(message);
        }
        else{
            // Try to launch a message or emission thread is null
        }
    }
}