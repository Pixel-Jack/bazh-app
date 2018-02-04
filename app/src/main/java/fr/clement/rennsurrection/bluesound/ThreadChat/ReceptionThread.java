package fr.clement.rennsurrection.bluesound.ThreadChat;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.clement.rennsurrection.bluesound.AddSpeaker.AddActivity;
import fr.clement.rennsurrection.bluesound.Objects.Speaker;
import fr.clement.rennsurrection.bluesound.SpeakerSettings.SpeakerActivity;
import fr.clement.rennsurrection.bluesound.Main.MainActivity;

/**
 * Created by Clément on 06/02/2017.
 */

public class ReceptionThread implements Runnable {

    private final static String ACTION_STATE_CHAT_CHANGED = "fr.clement.rennsurrection.testbluetooth.ACTION_STATE_CHAT_CHANGED";
    private final static String EXTRA_STATE_CHAT = "fr.clement.rennsurrection.testbluetooth.EXTRA_STATE_CHAT";
    private final static int STATE_CHAT_DISCONNECTED_BASE = 2;

    private final int MESSAGE = 0;
    private InputStream is = null;
    private Handler handlerReception = null;
    private Context context = null;
    private Intent intentComStateChat = null;
    private final String TAG = "INFOS";
    private Boolean reception = true;

    public ReceptionThread(InputStream is, Handler handler, Context eContext){
        this.is = is;
        this.handlerReception = handler;
        this.context = eContext;
    }


    public void run(){
        while(reception){
            try {
                byte buffer[]= new byte[200];
                int bytes_read = 0;
                bytes_read = is.read(buffer);
                String received = new String(buffer, 0, bytes_read);
                Message msg = handlerReception.obtainMessage(MESSAGE, received);
                handlerReception.sendMessage(msg);
                traitementOrdre(received);
            } catch (IOException e) {
                if(reception){
                    intentComStateChat = new Intent(ACTION_STATE_CHAT_CHANGED);
                    intentComStateChat.putExtra(EXTRA_STATE_CHAT, STATE_CHAT_DISCONNECTED_BASE);
                    context.sendBroadcast(intentComStateChat);
                    cancel();
                }
            }
        }
    }

    public void cancel(){
        reception = false;
        if(is != null){
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    private void traitementOrdre(String order){
        Intent intentComTraitementOrdre ;
        /*
        public final static int STATE_CONNECTED = 0;
        public final static int SPEAKER_DISCONNECTED = 1;
        public final static int SPEAKER_TO_DISCONNECT = 2;
        public final static int LIST_SPEAKER = 3;
        public final static int STOP_LISTE_SPEAKER = 4;
        public final static int PAIR = 5;
        public final static int PAIR_ERROR = 6;
        public final static int PAIR_SUCESS = 7;
        public final static int VOLUME = 8;
        public final static int ERREUR_DECO = 9;
        * 0,NameSpeaker1,AddressSpeaker1,Volume1;NameSpeaker2,AddressSpeaker2,Volume2;,,,;,,,;
        * 1,AddressSpeaker
        * 2,AddressSpeaker
        * 3,NameSpeaker1,AddressSpeaker1 #several times
        * 4
        * 5,AddressSpeaker
        * 6
        * 7,NameSpeaker,AddressSpeaker
        * 8,AddressSpeaker,Volume
        * 9,AddressSpeaker*/
        String regex = "([0-9]+)(?:,(.*))?";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(order);
        String[] listeInfos = null;
        Matcher m_modif;
        if(m.matches()){
            int identifiant = Integer.parseInt(m.group(1));
            switch (identifiant){
                case(MainActivity.SPEAKER_DISCONNECTED):
                    regex = "((?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})";
                    p = Pattern.compile(regex);
                    if(m.groupCount()>1){
                        m_modif = p.matcher(m.group(2));
                        if(m_modif.matches()){
                            String address = m_modif.group(1);
                            intentComTraitementOrdre = new Intent(SpeakerActivity.NOTIFY_SPEAKER_DISCO);
                            intentComTraitementOrdre.putExtra(SpeakerActivity.EXTRA_ADDRESS,address);
                            intentComTraitementOrdre.putExtra(SpeakerActivity.EXTRA_INFOS, SpeakerActivity.DISCONNECTION_SUCCESS);
                            context.sendBroadcast(intentComTraitementOrdre);

                            intentComTraitementOrdre = new Intent(MainActivity.ACTION_MODIFY_LIST_SPEAKER_MAIN);
                            intentComTraitementOrdre.putExtra(MainActivity.EXTRA_ADDRESS_TO_SUPPRESS, address);
                            context.sendBroadcast(intentComTraitementOrdre);
                        }
                    }
                    break;
                case(MainActivity.LIST_SPEAKER):
                    regex = "(.*),((?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})";
                    p = Pattern.compile(regex);
                    if(m.groupCount()>1) {
                        m_modif = p.matcher(m.group(2));
                        if(m_modif.matches()){
                            intentComTraitementOrdre = new Intent(AddActivity.ACTION_MODIF_ADDACTIVITY);
                            Speaker e = new Speaker(m_modif.group(1),m_modif.group(2),100);
                            intentComTraitementOrdre.putExtra(AddActivity.EXTRA_TYPE, AddActivity.SPEAKER_PAIRABLE);
                            intentComTraitementOrdre.putExtra(MainActivity.EXTRA_SPEAKER, e);
                            context.sendBroadcast(intentComTraitementOrdre);
                        }
                    }else{
                        // Log.w(TAG, "traitementOrdre/PAIR_SUCESS Problème de mise en forme de l'information envoyé -> "+order);
                    }
                    break;
                case(MainActivity.PAIR_SUCCESS):
                    regex = "(.*),((?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})";
                    p = Pattern.compile(regex);
                    if(m.groupCount()>1){
                        m_modif = p.matcher(m.group(2));
                        if(m_modif.matches()){
                            Speaker e = new Speaker(m_modif.group(1),m_modif.group(2),100);
                            intentComTraitementOrdre = new Intent(AddActivity.ACTION_MODIF_ADDACTIVITY);
                            intentComTraitementOrdre.putExtra(AddActivity.EXTRA_TYPE, AddActivity.PAIR_SUCESS);
                            intentComTraitementOrdre.putExtra(MainActivity.EXTRA_SPEAKER, e);
                            context.sendBroadcast(intentComTraitementOrdre);
                            intentComTraitementOrdre = new Intent(MainActivity.ACTION_MODIFY_LIST_SPEAKER_MAIN);
                            intentComTraitementOrdre.putExtra(MainActivity.EXTRA_SPEAKER, e);
                            context.sendBroadcast(intentComTraitementOrdre);
                        }
                    }else{
                        // Log.w(TAG, "traitementOrdre/PAIR_SUCESS Problème de mise en forme de l'information envoyé -> "+order);
                    }
                    break;

                case(MainActivity.PAIR_FAIL):
                    intentComTraitementOrdre = new Intent(AddActivity.ACTION_MODIF_ADDACTIVITY);
                    intentComTraitementOrdre.putExtra(AddActivity.EXTRA_TYPE, AddActivity.PAIR_ERROR);
                    context.sendBroadcast(intentComTraitementOrdre);
                    break;

                case(MainActivity.STATE_CONNECTED):
                    int nb_Dongle = 0;
                    ArrayList<Speaker> liste_enceintes_connectees = new ArrayList<>();
                    Speaker enceinte;
                    regex = "(.*?),(.*?),(.*?);";
                    p = Pattern.compile(regex);
                    if(m.groupCount()>1) {
                        m_modif = p.matcher(m.group(2));
                        while(m_modif.find()){
                            nb_Dongle +=1;
                            if(m_modif.group(0).equals(",,,;") || m_modif.group(0).equals(",,;")){
                                // Log.i("INFOS", "dongle " + nb_Dongle +" non pairé");
                            }else{
                                enceinte = new Speaker(m_modif.group(1),m_modif.group(2),Integer.parseInt(m_modif.group(3)));
                                liste_enceintes_connectees.add(enceinte);
                            }
                        }
                        intentComTraitementOrdre = new Intent(MainActivity.ACTION_MODIFY_LIST_SPEAKER_MAIN);
                        intentComTraitementOrdre.putExtra(MainActivity.EXTRA_DONGLE_NUMBER, nb_Dongle);
                        intentComTraitementOrdre.putExtra(MainActivity.EXTRA_SPEAKER_CONNECTED, liste_enceintes_connectees);
                        context.sendBroadcast(intentComTraitementOrdre);
                    }else{
                        //Log.w(TAG, "traitementOrdre/PAIR_SUCESS -> "+order);
                    }
                    break;

                case (MainActivity.ERREUR_DECO):
                    regex = "((?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})";
                    p = Pattern.compile(regex);
                    if(m.groupCount()>1){
                        m_modif = p.matcher(m.group(2));
                        if(m_modif.matches()){
                            String address = m_modif.group(1);
                            intentComTraitementOrdre = new Intent(SpeakerActivity.NOTIFY_SPEAKER_DISCO);
                            intentComTraitementOrdre.putExtra(SpeakerActivity.EXTRA_ADDRESS,address);
                            intentComTraitementOrdre.putExtra(SpeakerActivity.EXTRA_INFOS, SpeakerActivity.DISCONNECTION_FAIL);
                            context.sendBroadcast(intentComTraitementOrdre);
                        }
                    }
                    break;
            }
        }
    }
}