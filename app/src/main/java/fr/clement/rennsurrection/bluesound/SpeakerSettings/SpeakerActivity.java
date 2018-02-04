package fr.clement.rennsurrection.bluesound.SpeakerSettings;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;

import fr.clement.rennsurrection.bluesound.Objects.Speaker;
import fr.clement.rennsurrection.bluesound.Main.MainActivity;
import fr.clement.rennsurrection.bluesound.R;

public class SpeakerActivity extends AppCompatActivity {
    private Speaker speaker_parcel = null;
    private TextView number = null;
    private String name = null;
    private SeekArc vol1 = null;
    private int p = 0;
    private String value = "";
    private TextView textView_Volume = null;
    private Button buttonDeconnecter = null;


    private final static String EXTRA_SPEAKER = "extra_speaker";
    private final static String SPEAKER = "fr.clement.rennsurrection.testbluetooth.speaker";

    private IntentFilter filterChatDisconnected = null;
    private BroadcastReceiver broadcastReceiverChatDisconnected = null;

    public final static String NOTIFY_SPEAKER_DISCO = "fr.clement.rennsurrection.testbluetooth.NOTIFY_SPEAKER_DISCO";
    public final static String EXTRA_ADDRESS = "extra_address";
    public final static String EXTRA_INFOS = "extra_INFOS";
    public final static String DISCONNECTION_SUCCESS = "disconnection_success";
    public final static String DISCONNECTION_FAIL = "disconnection_fail";
    private IntentFilter filterSpeakerDisconnected = null;
    private BroadcastReceiver broadcastReceiverSpeakerDisconnected = null;

    public Context ctx = this;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(SPEAKER, speaker_parcel);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_settings);

        speaker_parcel = getIntent().getParcelableExtra(EXTRA_SPEAKER);

        vol1 = (SeekArc) findViewById(R.id.seekArc);
        vol1.setProgress(speaker_parcel.getVolume());
        number = (TextView) findViewById(R.id.enceinte);
        textView_Volume = (TextView) findViewById(R.id.textView_volume);
        textView_Volume.setText(Integer.toString(speaker_parcel.getVolume()));

        name = speaker_parcel.getNom();
        number.setText(name);

        vol1.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                value = Integer.toString(seekArc.getProgress());
                textView_Volume.setText(value);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                p = seekArc.getProgress();
                speaker_parcel.setVolume(p);
                value = Integer.toString(p);
                textView_Volume.setText(value);
                MainActivity.volumeSpeaker(speaker_parcel, value);
            }
        });

        buttonDeconnecter = (Button) findViewById(R.id.buttonDeconnecter);
        buttonDeconnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.deconnectionSpeaker(speaker_parcel);
            }
        });

        filterChatDisconnected = new IntentFilter(MainActivity.NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD);
        broadcastReceiverChatDisconnected = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(action.equals(MainActivity.NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD)){
                    DialogInterface.OnClickListener reponseRetour = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopEnceinteActivity();
                        }
                    };
                    AlertDialog.Builder builderDeconnexionEnceinte = new AlertDialog.Builder(context);
                    builderDeconnexionEnceinte.setTitle("Connection lost");
                    builderDeconnexionEnceinte.setIcon(getResources().getDrawable(R.drawable.ic_stop,getTheme()));
                    builderDeconnexionEnceinte.setMessage(getResources().getString(R.string.backtoHome));
                    builderDeconnexionEnceinte.setPositiveButton("OK", reponseRetour);
                    builderDeconnexionEnceinte.show();
                }
            }
        };

        filterSpeakerDisconnected = new IntentFilter(NOTIFY_SPEAKER_DISCO);
        broadcastReceiverSpeakerDisconnected = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if(action.equals(NOTIFY_SPEAKER_DISCO)){
                    String addresse = intent.getStringExtra(EXTRA_ADDRESS);
                    if(addresse.equals(speaker_parcel.getAddress())){
                        if(intent.getStringExtra(EXTRA_INFOS).equals(DISCONNECTION_SUCCESS)){
                            stopEnceinteActivityAvecDeconnexion(speaker_parcel);
                        }else if(intent.getStringExtra(EXTRA_INFOS).equals(DISCONNECTION_FAIL)){
                            Snackbar.make(findViewById(R.id.content_enceinte), getResources().getString(R.string.deco_echouee),
                                    Snackbar.LENGTH_LONG).show();
                        }else {
                            // We received a weird order
                        }
                    }
                }
            }
        };

        // AppBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator =
                    VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiverChatDisconnected,filterChatDisconnected);
        registerReceiver(broadcastReceiverSpeakerDisconnected, filterSpeakerDisconnected);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverChatDisconnected);
        unregisterReceiver(broadcastReceiverSpeakerDisconnected);
    }

    @Override
    public boolean onSupportNavigateUp() {
        stopEnceinteActivity();
        return true;
    }

    protected void stopEnceinteActivity(){
        Intent intent = new Intent();
        intent.putExtra(SPEAKER, speaker_parcel);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    protected void stopEnceinteActivityAvecDeconnexion(Speaker e){
        Intent intentRetour = new Intent();
        setResult(RESULT_OK, intentRetour);
        DialogInterface.OnClickListener reponseOK = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        AlertDialog.Builder builderDeconnexionEnceinte = new AlertDialog.Builder(ctx);
        builderDeconnexionEnceinte.setIcon(getResources().getDrawable(R.drawable.ic_info,getTheme()));

        builderDeconnexionEnceinte.setTitle("Confirmation ");
        builderDeconnexionEnceinte.setMessage("The device "+e.getNom()+" has been deconnected !\nBack to home !");
        builderDeconnexionEnceinte.setPositiveButton("OK", reponseOK);
        builderDeconnexionEnceinte.show();
    }
}