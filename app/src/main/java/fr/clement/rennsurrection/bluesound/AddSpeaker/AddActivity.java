package fr.clement.rennsurrection.bluesound.AddSpeaker;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import fr.clement.rennsurrection.bluesound.Objects.Speaker;
import fr.clement.rennsurrection.bluesound.Main.MainActivity;
import fr.clement.rennsurrection.bluesound.R;

public class AddActivity extends AppCompatActivity implements SpeakerAdapterListener, DialogPair.DialogPairingListener {
    //BroadCast
    public static final String ACTION_MODIF_ADDACTIVITY = "fr.clement.rennsurrection.bluesound.action.modifaddactivity";
    public static final String EXTRA_TYPE = "TYPE";
    public  static final int EXTRA_ERROR = -1;
    public static final int SPEAKER_PAIRABLE = 0;
    public static final int PAIR_SUCESS = 1;
    public static final int PAIR_ERROR = 2;

    //requestcode
    private final static int CHOOSE_ENCEINTE_ADD = 1;

    public static final String speaker ="speaker";
    private ImageButton addImageButton = null;
    public static ArrayList<Speaker> listE = null;
    private static SpeakerAdapter adapter = null;

    //BroadCastReceiver in order to know if a device is available
    private IntentFilter filterSpeakerAvailable = null;
    private BroadcastReceiver broadcastReceiverSpeakerAvailable = null;

    //BroadCastReceiver in order to know if the chat has been disconnected
    private IntentFilter filterChatDisconnected = null;
    private BroadcastReceiver broadcastReceiverChatDisconnected = null;

    private Button buttonSearch = null;

    private DialogPair dialogPair = null;

    private Speaker itemSelected = null;

    private boolean isSearching = true;
    private boolean isPairing = false;

    private Context context = this;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(AddActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, i);
            if(isSearching){
                MainActivity.stopSearchingSpeaker();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_speaker);

        addImageButton = (ImageButton) findViewById(R.id.adapter_speaker_buton_add);
        
        Intent intent = new Intent();
        listE = new ArrayList<>();


        adapter = new SpeakerAdapter(this, listE);

        adapter.addListener(this);
        ListView list = (ListView) findViewById(R.id.ListView01);
        list.setAdapter(adapter);

        filterSpeakerAvailable = new IntentFilter(ACTION_MODIF_ADDACTIVITY);
        broadcastReceiverSpeakerAvailable = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(action.equals(ACTION_MODIF_ADDACTIVITY)){
                    int typeModif = intent.getIntExtra(EXTRA_TYPE,EXTRA_ERROR);
                    switch (typeModif){
                        case SPEAKER_PAIRABLE:
                            // We can add a device even if we are waiting the confirmation of another device
                            // So if we cancel a demand of pairing, we can see the device which has been connected while we waited.
                            Speaker e = intent.getParcelableExtra(MainActivity.EXTRA_SPEAKER);
                            addSpeaker(e);
                            break;
                        case PAIR_SUCESS:
                            if(isPairing){
                                Speaker enceinte_paired = intent.getParcelableExtra(MainActivity.EXTRA_SPEAKER);

                                pairSuccess(enceinte_paired);
                            }else {
                                // We have received a pairing demand yet we didn't ask anything.
                            }
                            break;
                        case PAIR_ERROR:
                            if(isPairing){
                                pairFail();
                                isPairing = false;
                            }else {
                                // We received an error yet we didn't ask anything.
                            }
                            break;
                    }
                }
            }
        };

        filterChatDisconnected = new IntentFilter(MainActivity.NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD);
        broadcastReceiverChatDisconnected = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(action.equals(MainActivity.NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD)){
                    DialogInterface.OnClickListener reponseRetour = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAddActivity();
                        }
                    };
                    AlertDialog.Builder builderConnexionLost = new AlertDialog.Builder(context);
                    builderConnexionLost.setIcon(getResources().getDrawable(R.drawable.ic_stop,getTheme()));
                    builderConnexionLost.setTitle(getResources().getString(R.string.connectionlost));
                    builderConnexionLost.setMessage(getResources().getString(R.string.backtoHome));
                    builderConnexionLost.setPositiveButton("OK", reponseRetour);
                    builderConnexionLost.show();
                }
            }
        };


        buttonSearch = (Button)findViewById(R.id.buttonRecherche);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStateSearch();
            }
        });


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black, getTheme());
            if (indicator != null) {
                indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            }
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent i = new Intent(AddActivity.this, MainActivity.class);
        setResult(RESULT_CANCELED, i);
        if(isSearching){
            MainActivity.stopSearchingSpeaker();
        }
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiverSpeakerAvailable, filterSpeakerAvailable);
        registerReceiver(broadcastReceiverChatDisconnected,filterChatDisconnected);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverSpeakerAvailable);
        unregisterReceiver(broadcastReceiverChatDisconnected);
    }

    protected void changeStateSearch(){
        if(isSearching){
            isSearching = false;
            MainActivity.stopSearchingSpeaker();
            buttonSearch.setText(getResources().getText(R.string.lancerRecherche));
        }
        else{
            clearListEnceinte();
            isSearching = true;
            MainActivity.getListSpeaker();
            buttonSearch.setText(getResources().getText(R.string.arreterRecherche));
        }
    }

    public void onClickName(Speaker item, int position) {
        if(isSearching){
            isSearching = false;
        }
        isPairing = true;
        itemSelected = item;
        isSearching = false;
        buttonSearch.setText(R.string.lancerRecherche);
        MainActivity.pairageEnceinte(itemSelected);
        showDialogPairage();
    }

    public static void addSpeaker(Speaker e){
        /*
        * If device already known (MAC) we just change its name
        */
        int index = isMACPresent(e);
        if( index != -1){
            Speaker speaker_to_modify = listE.get(index);
            speaker_to_modify.setName_speaker(e.getNom());
            listE.set(index,speaker_to_modify);
        }else{
            listE.add(e);
        }
        adapter.notifyDataSetChanged();
    }

    public static int isMACPresent(Speaker e){
        Speaker e_co;
        for (int i = 0; i<listE.size() ; i++){
            e_co = listE.get(i);
            if(e_co.getAddress().equals(e.getAddress())){
                return i;
            }
        }
        return -1;
    }

    public static void clearListEnceinte(){
        listE.clear();
        adapter.notifyDataSetChanged();
    }


    public void showDialogPairage() {
        //Create instance of the dialog fragment and show it
        dialogPair = new DialogPair();

        dialogPair.show(getFragmentManager(),"dialogPair");
    }


    public void pairSuccess(Speaker e){
        // First we check if the confirmation concern the device we are waiting for
        if(e.getAddress().equals(itemSelected.getAddress())){
            isPairing = false;
            Intent i = new Intent(AddActivity.this, MainActivity.class);
            i.putExtra(speaker, e);
            setResult(RESULT_OK, i);
            dialogPair.dismiss();
            finish();
        }else{
            // spontaneous connection
        }
    }



    public void pairFail(){
        clearListEnceinte();
        Toast.makeText(this, "Failed ", Toast.LENGTH_SHORT).show();
        dialogPair.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pairing failed");
        builder.setIcon(getResources().getDrawable(R.drawable.ic_action_warning,getTheme()));
        builder.setMessage("We can't paired with device");
        builder.setPositiveButton("back", null);
        builder.show();
    }

    protected void finishAddActivity(){
        if(dialogPair != null){
            if(dialogPair.isVisible()){
                dialogPair.dismiss();
            }
        }
        Intent i = new Intent(AddActivity.this, MainActivity.class);
        setResult(RESULT_CANCELED, i);
        finish();
    }

    public void onDialogPositiveClick(DialogFragment dialog){
        // useless
    }

    public void onDialogNegativeClick(DialogFragment dialog){
        MainActivity.cancelPair();
    }
}