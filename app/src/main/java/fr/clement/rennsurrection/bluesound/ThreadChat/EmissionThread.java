package fr.clement.rennsurrection.bluesound.ThreadChat;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Clément on 06/02/2017.
 */

public class EmissionThread implements Runnable {
    private OutputStream outputStream;
    private Context context = null;

    private final static String ACTION_STATE_CHAT_CHANGED = "fr.clement.rennsurrection.testbluetooth.ACTION_STATE_CHAT_CHANGED";
    private final static String EXTRA_STATE_CHAT = "fr.clement.rennsurrection.testbluetooth.EXTRA_STATE_CHAT";
    private  final static  int STATE_CHAT_DISCONNECTED_CELL = 3;

    private Intent intentComm = null;
    private boolean emission = true;

    public EmissionThread(OutputStream os, Context eContext){
        outputStream = os;
        context = eContext;
    }

    public void run(){
    }

    public synchronized void sendMessage(String message) {
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            if(emission){
                intentComm = new Intent(ACTION_STATE_CHAT_CHANGED);
                intentComm.putExtra(EXTRA_STATE_CHAT, STATE_CHAT_DISCONNECTED_CELL);
                cancel();
            }
        }
    }

    public void cancel(){
        emission = false;
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}