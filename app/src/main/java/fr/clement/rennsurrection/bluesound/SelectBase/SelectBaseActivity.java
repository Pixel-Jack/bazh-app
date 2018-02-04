package fr.clement.rennsurrection.bluesound.SelectBase;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;

import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Clément on 05/02/2017.
 */
public class SelectBaseActivity extends AppCompatActivity implements DeviceAdapterListener {
    private final String TAG = "INFOS";

    private final static int CHOOSE_BASE = 0;
    public final static String DEVICE = "fr.clement.rennsurrection.testbluetooth.device";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_base);

        Intent i = getIntent();

        // We get the number of device known
        int nb_pairage = i.getIntExtra("fr.clement.rennsurrection.testbluetooth.NUMBER_SPEAKER",0);

        BluetoothDevice device = null;
        ArrayList<BluetoothDevice> arrayBluetoothDevice = new ArrayList<BluetoothDevice>();
        String nameDevice = null;
        for(int j = 0 ; j < nb_pairage ; j++){
            nameDevice = "fr.clement.rennsurrection.testbluetooth.SPEAKER" + j;
            device = i.getParcelableExtra(nameDevice);
            arrayBluetoothDevice.add(device);
        }

        DeviceAdapter adapter = new DeviceAdapter(this, arrayBluetoothDevice);

        adapter.addListener(this);

        ListView list = (ListView)findViewById(R.id.listView);

        list.setAdapter(adapter);

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

    public void onClickDevice(final BluetoothDevice item, int position){ //changement d'item en final
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Device selected : ");
        builder.setMessage("You have chosen : " + item.getName());
        DialogInterface.OnClickListener reponseOuiListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent result = new Intent();
                result.putExtra(DEVICE,item);
                setResult(RESULT_OK,result);
                finish();
            }
        };

        builder.setIcon(getResources().getDrawable(R.drawable.ic_info,getTheme()));
        builder.setPositiveButton("Yes", reponseOuiListener);
        builder.setNegativeButton("No",null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}