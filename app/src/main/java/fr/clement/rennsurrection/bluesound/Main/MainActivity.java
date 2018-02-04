package fr.clement.rennsurrection.bluesound.Main;


import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.clement.rennsurrection.bluesound.AddSpeaker.AddActivity;
import fr.clement.rennsurrection.bluesound.AddSpeaker.CoAdapterListener;
import fr.clement.rennsurrection.bluesound.SelectBase.SelectBaseActivity;
import fr.clement.rennsurrection.bluesound.Objects.Speaker;
import fr.clement.rennsurrection.bluesound.SpeakerSettings.SpeakerActivity;
import fr.clement.rennsurrection.bluesound.R;
import fr.clement.rennsurrection.bluesound.ThreadChat.ConnectThread;


public class MainActivity extends AppCompatActivity implements CoAdapterListener {
    public final static int STATE_CONNECTED = 0;
    public final static int SPEAKER_DISCONNECTED = 1;
    public final static int SPEAKER_TO_DISCONNECT = 2;
    public final static int LIST_SPEAKER = 3;
    public final static int STOP_LISTE_SPEAKER = 4;
    public final static int PAIR = 5;
    public final static int PAIR_FAIL = 6;
    public final static int PAIR_SUCCESS = 7;
    public final static int VOLUME = 8;
    public final static int ERREUR_DECO = 9;
    public final String TAG = "INFO";

    private BroadcastReceiver bluetoothStateReceiver = null;
    private IntentFilter filter1 = null;

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private TextView textViewBluetoothState = null;
    private ImageButton buttonSettingBluetooth = null;
    private TextView textViewPair = null;
    private Button buttonConnection = null;
    private static TextView textViewChatState = null;
    private static TextView textViewChat = null;
    private Button buttonSend = null;
    private EditText editMessage = null;
    private LinearLayout layoutChat = null;
    private LinearLayout layout_Connection_ON = null;
    private ImageButton buttonShowChat = null;
    private ImageButton buttonRaZ = null;

    // MODIFICATION in order to install the pipe
    // Manage communications with the reception thread
    final private Handler handlerReception = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            writeChat("\nServer : " + msg.obj);
        }
    };


    // Id of activityOnResult to choose a base
    private final static int CHOOSE_BASE = 0;
    private final static int CHOOSE_SPEAKER_ADD = 1;
    public final static String DEVICE = "fr.clement.rennsurrection.testbluetooth.device";

    // Base chosen
    private BluetoothDevice base = null;

    //Receiver for the chat state :
    private final static int ERROR_CHAT = -1;
    private final static String ACTION_STATE_CHAT_CHANGED = "fr.clement.rennsurrection.testbluetooth.ACTION_STATE_CHAT_CHANGED";
    private final static String EXTRA_STATE_CHAT = "fr.clement.rennsurrection.testbluetooth.EXTRA_STATE_CHAT";
    public final static int STATE_CHAT_CONNECTED = 0;
    public final static int STATE_CHAT_DISCONNECTED = 1;
    public final static int STATE_CHAT_DISCONNECTED_BASE = 2;
    public final static int STATE_CHAT_DISCONNECTED_CELL = 3;
    private BroadcastReceiver broadcastReceiverStateChat = null;
    private IntentFilter filterStateChat = null;

    private final int CODE_BLUETOOTH_SETTINGS = 85;

    // Connection thread to the base
    private static ConnectThread connectThread = null;
    private Boolean connectionWorking = false;
    private Boolean isConnecting = false;

    // List of connected device
    private static ArrayList<Speaker> listeSpeakerConnected = new ArrayList<Speaker>();


    public final static String EXTRA_SPEAKER = "extra_speaker";
    private final static String SPEAKER = "fr.clement.rennsurrection.testbluetooth.speaker";
    private final static int CHOOSE_TO_DISCO_SPEAKER = 3;

    private ConnectionAdapter adapterListSpeakerConnected = null;
    private ListView listView_SpeakerConnected = null;

    //BroadCast for Ajout and SpeakerActivity
    public final static String NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD = "fr.clement.rennsurrection.testbluetooth.NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD";


    //BroadCast in order to show devices at first connection
    public static final String ACTION_MODIFY_LIST_SPEAKER_MAIN = "fr.clement.rennsurrection.bluesound.action.modifyListSpeakerMain";
    public static final String EXTRA_DONGLE_NUMBER = "DONGLE_NUMBER";
    public static final String EXTRA_SPEAKER_CONNECTED = "SPEAKER_CONNECTED";
    public static final String EXTRA_ADDRESS_TO_SUPPRESS = "ADDRESS_TO_SUPPRESS";
    private int nb_Dongle;
    private BroadcastReceiver firstInformationReceiver = null;
    private IntentFilter filterFirstInfos = null;


    // add device button:
    private FloatingActionButton buttonAddSpeaker = null;

    private ProgressBar progressBar_connexion = null;


    private LinearLayout stateView = null;
    private Context context = this;

    private RelativeLayout relative_No_connection = null;
    private RelativeLayout relative_No_speaker = null;

    private TextView textViewNbDongle = null;

    // BroadCast to know the A2DP state
    private BroadcastReceiver bluetoothProxyA2DPConnected = null;
    private IntentFilter filterProxyA2DPConnected = null;
    private Boolean isBroadcastReceiverbluetoothProxyA2DPConnected_Registered = false;


    // ServiceListener which will know when the AD2P state changes
    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP) {
                final List<BluetoothDevice> btlist = proxy.getConnectedDevices();

                if (btlist.size() == 1) {
                    final BluetoothDevice basePaired = btlist.get(0);

                    base = basePaired;
                    String textStatePaire = "The phone is paired to " + base.getName();
                    textViewPair.setText(textStatePaire);
                    buttonConnection.setText(getResources().getText(R.string.texteButtonChat));
                    if (!isConnecting) {
                        buttonConnection.setVisibility(View.VISIBLE);
                    }
                    buttonConnection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            connectionChat();
                        }
                    });
                } else if (btlist.size() == 0) {
                    // An AD2P connection is declared or there is no device connected
                } else {
                    buttonConnection.setVisibility(View.VISIBLE);
                    buttonConnection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int nb_pair = btlist.size();
                            Intent intentChooseBase = new Intent(MainActivity.this, SelectBaseActivity.class);
                            intentChooseBase.putExtra("fr.clement.rennsurrection.testbluetooth.NOMBER_SPEAKER", nb_pair);
                            int i = 0;
                            for (BluetoothDevice device : btlist) {
                                String nameExtra = "fr.clement.rennsurrection.testbluetooth.SPEAKER" + i;
                                intentChooseBase.putExtra(nameExtra, device);
                                i++;
                            }
                            startActivityForResult(intentChooseBase, CHOOSE_BASE);
                        }
                    });
                }
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Toast toast = Toast.makeText(context, "Deconnection", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Leave confirmation");
            builder.setIcon(getResources().getDrawable(R.drawable.ic_action_warning, getTheme()));

            builder.setMessage("Do you want to leave ?");

            DialogInterface.OnClickListener reponseOuiListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();

                }
            };
            builder.setPositiveButton("Yes", reponseOuiListener);
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterProxyA2DPConnected = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filterProxyA2DPConnected.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        bluetoothProxyA2DPConnected = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(BluetoothA2dp.EXTRA_STATE)) {
                    int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                    if (state == BluetoothA2dp.STATE_CONNECTED) {
                        BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        base = bluetoothDevice;
                        String texteEtatPairage = "Cellphone paired to " + base.getName();

                        textViewPair.setText(texteEtatPairage);
                        buttonConnection.setText(getResources().getText(R.string.texteButtonChat));
                        buttonConnection.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                connectionChat();
                            }
                        });
                        finishActivity(CODE_BLUETOOTH_SETTINGS);
                        connectionChat();
                    }
                }
            }

        };
        textViewBluetoothState = (TextView) findViewById(R.id.textViewBluetoothState);

        buttonSettingBluetooth = (ImageButton) findViewById(R.id.buttonSettingBluetooth);
        buttonSettingBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBroadcastReceiverbluetoothProxyA2DPConnected_Registered) {
                    unregisterReceiver(bluetoothProxyA2DPConnected);
                }
                Intent intentBluetooth = new Intent();
                intentBluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(intentBluetooth, CODE_BLUETOOTH_SETTINGS);
            }
        });

        textViewPair = (TextView) findViewById(R.id.textViewPair);

        buttonConnection = (Button) findViewById(R.id.buttonConnection);

        textViewChatState = (TextView) findViewById(R.id.textViewChatState);

        textViewChat = (TextView) findViewById(R.id.textViewChat);
        textViewChat.setMovementMethod(new ScrollingMovementMethod());

        layoutChat = (LinearLayout) findViewById(R.id.layoutChat);

        layout_Connection_ON = (LinearLayout) findViewById(R.id.layout_Connection_ON);

        relative_No_connection = (RelativeLayout) findViewById(R.id.relative_No_connexion);
        relative_No_speaker = (RelativeLayout) findViewById(R.id.relative_No_speaker);

        textViewNbDongle = (TextView) findViewById(R.id.nb_Dongle);

        buttonShowChat = (ImageButton) findViewById(R.id.button_CHAT_VISIBLE);
        buttonShowChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutChat.getVisibility() == View.VISIBLE) {
                    layoutChat.setVisibility(View.GONE);
                } else {
                    layoutChat.setVisibility(View.VISIBLE);
                }
            }
        });


        buttonRaZ = (ImageButton) findViewById(R.id.buttonRaZ);
        buttonRaZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Reset : ");
                builder.setIcon(getResources().getDrawable(R.drawable.ic_action_warning, getTheme()));

                builder.setMessage("Do you want to reset ?");

                DialogInterface.OnClickListener reponseOuiListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rAZ();
                        bluetoothStateChange();

                    }
                };

                builder.setPositiveButton("Yes", reponseOuiListener);
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });
        //

        buttonAddSpeaker = (FloatingActionButton) findViewById(R.id.button_ADD_SPEAKER);
        buttonAddSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListSpeaker();
                Intent intentAddSpeaker = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intentAddSpeaker, CHOOSE_SPEAKER_ADD);
            }
        });
        buttonAddSpeaker.setVisibility(View.GONE);

        filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter1.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        bluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                final String action = i.getAction();
                Toast toast = null;
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = i.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            bluetoothStateChange();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            bluetoothStateChange();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.ERROR:
                            bluetoothStateChange();
                            break;
                    }
                } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                    int state = i.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_CONNECTED:
                            bluetoothStateChange();
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            bluetoothStateChange();
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTING:
                            break;
                        case BluetoothAdapter.ERROR:
                            bluetoothStateChange();
                            break;
                    }
                }
            }
        };

        buttonSend = (Button) findViewById(R.id.buttonEnvoyer);
        editMessage = (EditText) findViewById(R.id.editMessage);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMessage.getText().equals("")) {
                    String textChat = textViewChat.getText().toString();
                    String aEnvoyer = editMessage.getText().toString();
                    sendMessageConnectThread(aEnvoyer);
                    textChat += "\nMe : " + aEnvoyer;
                    textViewChat.setText(textChat);
                    editMessage.setText("");

                }
            }
        });

        filterStateChat = new IntentFilter();
        filterStateChat.addAction(ACTION_STATE_CHAT_CHANGED);
        broadcastReceiverStateChat = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                final String action = i.getAction();
                Toast toast;
                if (action.equals(ACTION_STATE_CHAT_CHANGED)) {
                    if (i.hasExtra(EXTRA_STATE_CHAT)) {
                        int state = i.getIntExtra(EXTRA_STATE_CHAT, ERROR_CHAT);
                        switch (state) {
                            case STATE_CHAT_CONNECTED:
                                break;
                            case STATE_CHAT_DISCONNECTED:
                                isConnecting = false;
                                connectionWorking = false;
                                textViewChatState.setText("Connection has failed");
                                layout_Connection_ON.setVisibility(View.GONE);
                                buttonShowChat.setVisibility(View.GONE);
                                relative_No_connection.setVisibility(View.VISIBLE);
                                progressBar_connexion.setVisibility(View.GONE);
                                // We show the button to allow a reconnection
                                buttonConnection.setVisibility(View.VISIBLE);
                                toast = Toast.makeText(c, "Connection to " + base.getName() + " has failed !", Toast.LENGTH_SHORT);
                                toast.show();
                                deconnection();
                                notifyStateChatChangedForChild();
                                break;

                            case STATE_CHAT_DISCONNECTED_BASE: // here this is the base which has cut the connection
                                isConnecting = false;
                                connectionWorking = false;
                                textViewChatState.setText("The base has cut the link !");
                                layout_Connection_ON.setVisibility(View.GONE);
                                buttonShowChat.setVisibility(View.GONE);
                                buttonConnection.setVisibility(View.VISIBLE);

                                relative_No_connection.setVisibility(View.VISIBLE);
                                relative_No_speaker.setVisibility(View.GONE);
                                toast = Toast.makeText(c, "The base has cut the link !", Toast.LENGTH_SHORT);
                                toast.show();
                                deconnection();
                                notifyStateChatChangedForChild();
                                break;

                            case STATE_CHAT_DISCONNECTED_CELL: // here this is the cellphone which cut the link (ex : bluetooth switch off)
                                isConnecting = false;
                                connectionWorking = false;
                                textViewChatState.setText("The cellphone has cut the link !");
                                layout_Connection_ON.setVisibility(View.GONE);
                                buttonShowChat.setVisibility(View.GONE);
                                relative_No_connection.setVisibility(View.VISIBLE);
                                buttonConnection.setVisibility(View.VISIBLE);

                                relative_No_connection.setVisibility(View.VISIBLE);
                                relative_No_speaker.setVisibility(View.GONE);
                                toast = Toast.makeText(c, "The cellphone has cut the link !", Toast.LENGTH_SHORT);
                                toast.show();
                                deconnection();
                                notifyStateChatChangedForChild();
                                break;

                            case ERROR_CHAT:
                                isConnecting = false;
                                connectionWorking = false;
                                relative_No_connection.setVisibility(View.VISIBLE);
                                relative_No_speaker.setVisibility(View.GONE);
                                buttonConnection.setVisibility(View.VISIBLE);
                                toast = Toast.makeText(c, "There was an issue in the connection management of the chat!", Toast.LENGTH_SHORT);
                                toast.show();
                                break;
                        }
                    } else {
                        // There was a call to the broadcastStateReceiver without any Extra in the itent
                    }

                }
            }
        };

        filterFirstInfos = new IntentFilter();
        filterFirstInfos.addAction(ACTION_MODIFY_LIST_SPEAKER_MAIN);
        firstInformationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                if (action.equals(ACTION_MODIFY_LIST_SPEAKER_MAIN)) {
                    if (intent.hasExtra(EXTRA_DONGLE_NUMBER)) {
                        isConnecting = false;
                        connectionWorking = true;
                        textViewChatState.setText(R.string.liaisonBaseOK);
                        buttonConnection.setVisibility(View.GONE);
                        progressBar_connexion.setVisibility(View.GONE);
                        layout_Connection_ON.setVisibility(View.VISIBLE);
                        buttonShowChat.setVisibility(View.VISIBLE);
                        relative_No_connection.setVisibility(View.GONE);
                        buttonSettingBluetooth.setVisibility(View.GONE);

                        nb_Dongle = 0;
                        textViewNbDongle.setText(Integer.toString(nb_Dongle));
                        buttonAddSpeaker.setVisibility(View.GONE);
                        relative_No_speaker.setVisibility(View.VISIBLE);
                        buttonSettingBluetooth.setVisibility(View.GONE);

                        Toast toast = Toast.makeText(context, "Connected to " + base.getName() + " !", Toast.LENGTH_SHORT);
                        toast.show();
                        if (isBroadcastReceiverbluetoothProxyA2DPConnected_Registered) {
                            unregisterReceiver(bluetoothProxyA2DPConnected);
                            isBroadcastReceiverbluetoothProxyA2DPConnected_Registered = false;
                        }

                        nb_Dongle = intent.getIntExtra(EXTRA_DONGLE_NUMBER, 0);
                        textViewNbDongle.setText(Integer.toString(nb_Dongle));

                        buttonAddSpeaker.setVisibility(View.VISIBLE);
                        if (intent.hasExtra(EXTRA_SPEAKER_CONNECTED)) {
                            ArrayList<Speaker> listeEnceintes = intent.getParcelableArrayListExtra(EXTRA_SPEAKER_CONNECTED);
                            for (Speaker enceinteCo :
                                    listeEnceintes) {
                                addSpeakerConnected(enceinteCo);
                            }
                        }

                    } else if (intent.hasExtra(EXTRA_SPEAKER)) {
                        Speaker enceinte_paired = intent.getParcelableExtra(EXTRA_SPEAKER);
                        addSpeakerConnected(enceinte_paired);
                    } else if (intent.hasExtra(EXTRA_ADDRESS_TO_SUPPRESS)) {
                        String adrToSup = intent.getStringExtra(EXTRA_ADDRESS_TO_SUPPRESS);
                        removeSpeaker(adrToSup);
                    }
                }
            }
        };

        listView_SpeakerConnected = (ListView) findViewById(R.id.listView_EnceintesCo);
        adapterListSpeakerConnected = new ConnectionAdapter(this, listeSpeakerConnected);
        adapterListSpeakerConnected.addListener(this);
        listView_SpeakerConnected.setAdapter(adapterListSpeakerConnected);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stateView = (LinearLayout) findViewById(R.id.state);
        if (bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) != BluetoothAdapter.STATE_CONNECTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (!isBroadcastReceiverbluetoothProxyA2DPConnected_Registered) {
                registerReceiver(bluetoothProxyA2DPConnected, filterProxyA2DPConnected);
                isBroadcastReceiverbluetoothProxyA2DPConnected_Registered = true;
            }
            builder.setTitle("Not connected");
            builder.setMessage("The cellphone isn't connected to a base.");
            DialogInterface.OnClickListener reponseOuiListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intentBluetooth = new Intent();
                    intentBluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intentBluetooth, CODE_BLUETOOTH_SETTINGS);
                }
            };
            builder.setIcon(getResources().getDrawable(R.drawable.ic_action_warning, getTheme()));
            builder.setPositiveButton("Bluetooth settings", reponseOuiListener);
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showState:
                if (item.isChecked()) {
                    item.setChecked(false);
                    stateView.setVisibility(View.GONE);
                } else {
                    item.setChecked(true);
                    stateView.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.setToZero:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Reset : ");
                builder.setMessage("Do you want to reset ?");
                builder.setIcon(getResources().getDrawable(R.drawable.ic_action_warning, getTheme()));
                DialogInterface.OnClickListener reponseOuiListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rAZ();
                        bluetoothStateChange();
                    }
                };
                builder.setPositiveButton("Yes", reponseOuiListener);
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetoothStateReceiver, filter1);
        registerReceiver(broadcastReceiverStateChat, filterStateChat);
        registerReceiver(firstInformationReceiver, filterFirstInfos);
        bluetoothStateChange();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(broadcastReceiverStateChat);
        unregisterReceiver(firstInformationReceiver);
        if (isBroadcastReceiverbluetoothProxyA2DPConnected_Registered) {
            unregisterReceiver(bluetoothProxyA2DPConnected);
        }
    }

    protected void bluetoothStateChange() {
        int state = bluetoothAdapter.getState();
        int mode = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        String textBluetoothState = "";
        if (state == BluetoothAdapter.STATE_ON) {
            textBluetoothState += "Bluetooth ON";
            buttonConnection.setVisibility(View.GONE);
            textViewPair.setVisibility(View.GONE);
        } else if (state == BluetoothAdapter.STATE_OFF) {
            textBluetoothState += "Bluetooth OFF";
            buttonConnection.setVisibility(View.GONE);
            textViewPair.setVisibility(View.GONE);
            rAZ();
        }
        if (mode == BluetoothAdapter.STATE_CONNECTED) {
            if (!connectionWorking) {
                textBluetoothState += "\nCellphone paired with an AD2P link";
                buttonSettingBluetooth.setVisibility(View.GONE);
                bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.A2DP);
                textViewPair.setVisibility(View.VISIBLE);
                layout_Connection_ON.setVisibility(View.GONE);
                buttonShowChat.setVisibility(View.GONE);
                relative_No_connection.setVisibility(View.VISIBLE);
            }
        } else if (mode == BluetoothAdapter.STATE_DISCONNECTED) {
            buttonSettingBluetooth.setVisibility(View.VISIBLE);
            textBluetoothState += "\nCellphone isn't paired with an AD2P link";
            textViewPair.setVisibility(View.GONE);
            rAZ();
        }
        textViewBluetoothState.setText(textBluetoothState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_BASE) {
            if (resultCode == RESULT_OK) {
                final BluetoothDevice basePaired = data.getParcelableExtra(DEVICE);
                base = basePaired;
                String texteEtatPairage = "Cellphone paired to " + basePaired.getName();
                textViewPair.setText(texteEtatPairage);
                buttonConnection.setText(getResources().getText(R.string.texteButtonChat));
                buttonConnection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        connectionChat();
                    }
                });
                connectionChat();
            }
        } else if (requestCode == CHOOSE_SPEAKER_ADD) {
            if (resultCode == RESULT_OK) {
                Speaker e = data.getParcelableExtra(AddActivity.speaker);
                addSpeakerConnected(e);
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == CHOOSE_TO_DISCO_SPEAKER) {
            if (resultCode == RESULT_OK) {
            } else if (resultCode == RESULT_CANCELED) {
                Speaker e = data.getParcelableExtra(SPEAKER);
                changeAdapterListSpeakers(e);
            }
        }
    }

    protected void rAZ() {
        endConnectThread();
        textViewBluetoothState.setText(R.string.textViewBluetoothStateZero);
        textViewPair.setText(R.string.textViewPairageZero);
        buttonConnection.setText(R.string.buttonChatZero);
        textViewChatState.setText(R.string.textViewChatStateZero);
        textViewChat.setText("");
        layout_Connection_ON.setVisibility(View.GONE);
        relative_No_connection.setVisibility(View.VISIBLE);
        relative_No_speaker.setVisibility(View.GONE);
        buttonShowChat.setVisibility(View.GONE);
        buttonAddSpeaker.setVisibility(View.GONE);
        isBroadcastReceiverbluetoothProxyA2DPConnected_Registered = false;
        isConnecting = false;
        base = null;
        buttonSettingBluetooth.setVisibility(View.VISIBLE);
        nb_Dongle = 0;
        textViewNbDongle.setText(R.string.valeurNBDongleZero);
        listeSpeakerConnected.clear();
        if (bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothAdapter.STATE_CONNECTED) {
            bluetoothAdapter.disable();
        }
    }

    protected void endConnectThread() {
        /**
         * That will launch the end of the thread connection with the base
         * which will close emission thread and reception thread
         **/
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        connectionWorking = false;
    }


    protected void connectionChat() {
        progressBar_connexion = (ProgressBar) findViewById(R.id.progressBarConnexion);
        progressBar_connexion.setVisibility(View.VISIBLE);
        isConnecting = true;
        buttonConnection.setVisibility(View.GONE);
        connectThread = new ConnectThread(bluetoothAdapter, base, handlerReception, this);
        connectThread.start();
    }

    public static void sendMessageConnectThread(String message) {
        if (connectThread != null) {
            connectThread.sendMessage(message);
        } else {
            // Try to launch a message while the connection is closed
        }
    }

    public void onClickName(Speaker item, int position) {
        Intent i = new Intent(MainActivity.this, SpeakerActivity.class);
        i.putExtra(EXTRA_SPEAKER, item);
        startActivityForResult(i, CHOOSE_TO_DISCO_SPEAKER);
    }

    private void changeAdapterListSpeakers() {
        adapterListSpeakerConnected.notifyDataSetChanged();
    }

    private void changeAdapterListSpeakers(Speaker e) {
        int index = indexOfInListSpeakersConnected(e.getAddress());
        listeSpeakerConnected.set(index, e);
        changeAdapterListSpeakers();
    }

    private int isMACPresent(Speaker e) {
        Speaker e_co;
        for (int i = 0; i < listeSpeakerConnected.size(); i++) {
            e_co = listeSpeakerConnected.get(i);
            if (e_co.getAddress().equals(e.getAddress())) {
                return i;
            }
        }
        return -1;
    }

    private void addSpeakerConnected(Speaker e) {
        int index = isMACPresent(e);
        if (index != -1) {
            Speaker speaker_to_modify = listeSpeakerConnected.get(index);
            speaker_to_modify.setName_speaker(e.getNom());
            listeSpeakerConnected.set(index, speaker_to_modify);
        } else {
            listeSpeakerConnected.add(e);
            relative_No_speaker.setVisibility(View.GONE);
            if (listeSpeakerConnected.size() == nb_Dongle) {
                buttonAddSpeaker.setVisibility(View.GONE);
            }
        }
        changeAdapterListSpeakers();
    }

    private void removeSpeaker(Speaker e) {
        Speaker speaker = null;
        int index = indexOfInListSpeakersConnected(e.getAddress());
        if (index == -1) {
            // Try to suppress a device which isn't registered
        } else {
            listeSpeakerConnected.remove(index);
            changeAdapterListSpeakers();
            if (listeSpeakerConnected.size() == 0) {
                relative_No_speaker.setVisibility(View.VISIBLE);
            }
            if (nb_Dongle > 0) {
                buttonAddSpeaker.setVisibility(View.VISIBLE);
            }
        }
    }

    private void removeSpeaker(String adr) {
        Speaker speaker = null;
        int index = indexOfInListSpeakersConnected(adr);
        if (index == -1) {
            // Try to suppress a device which isn't registered
        } else {
            listeSpeakerConnected.remove(index);
            changeAdapterListSpeakers();
            if (listeSpeakerConnected.size() == 0) {
                relative_No_speaker.setVisibility(View.VISIBLE);
            }
            if (nb_Dongle > 0) {
                buttonAddSpeaker.setVisibility(View.VISIBLE);
            }
        }
    }

    private int indexOfInListSpeakersConnected(String adr) {
        /**
         * Return : index of the device in the list of connected device
         * Return : -1 if not present and -2 if the list of connected device if null
         * Search based on the unique MAC address
         */
        Speaker speaker_i;
        if (listeSpeakerConnected == null) {
            return -2;
        } else {
            for (int i = 0; i < listeSpeakerConnected.size(); i++) {
                speaker_i = listeSpeakerConnected.get(i);
                if (speaker_i.getAddress().equals(adr)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private void deconnection() {
        endConnectThread();
        listeSpeakerConnected.clear();
        changeAdapterListSpeakers();
        textViewChat.setText("");
    }

    private void notifyStateChatChangedForChild() {
        Intent chatDisconnected = new Intent(NOTIFY_STATE_CHAT_CHANGED_FOR_CHILD);
        this.sendBroadcast(chatDisconnected);
    }


    // Public methods :
    public static void writeChat(String message) {
        String chat = textViewChat.getText().toString();
        chat += message;
        textViewChat.setText(chat);
    }

    public static void deconnectionSpeaker(Speaker speaker) {
        String order = SPEAKER_TO_DISCONNECT + "," + speaker.getAddress();
        sendMessageConnectThread(order);
    }

    public static void getListSpeaker() {
        String order = Integer.toString(LIST_SPEAKER);
        sendMessageConnectThread(order);
        writeChat("\nCellphone : " + order);
    }

    public static void stopSearchingSpeaker() {
        String order = Integer.toString(STOP_LISTE_SPEAKER);
        sendMessageConnectThread(order);
        writeChat("\nCellphone : " + order);
    }

    public static void pairageEnceinte(Speaker speaker) {
        String order = PAIR + "," + speaker.getAddress();
        connectThread.sendMessage(order);
        writeChat("\nCellphone : " + order);
    }

    public static void cancelPair() {
        String order = Integer.toString(PAIR_FAIL);
        sendMessageConnectThread(order);
        writeChat("\nCellphone : " + order);
    }

    public static void volumeSpeaker(Speaker speaker, String valeur) {
        String order = VOLUME + "," + speaker.getAddress() + "," + valeur;
        sendMessageConnectThread(order);
        writeChat("\nCellphone : " + order);
    }
}