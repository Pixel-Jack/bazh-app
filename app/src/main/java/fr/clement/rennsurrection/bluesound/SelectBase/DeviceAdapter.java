package fr.clement.rennsurrection.bluesound.SelectBase;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Clément on 05/02/2017.
 */

public class DeviceAdapter extends BaseAdapter{
    private ArrayList<BluetoothDevice> arrayDevice = null;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<DeviceAdapterListener> deviceAdapterListenerArrayList = new ArrayList<DeviceAdapterListener>();
    public DeviceAdapter(Context c, ArrayList<BluetoothDevice> arrayList){
        context = c;
        arrayDevice = arrayList;
        inflater = LayoutInflater.from(context);
    }

    public void addListener(DeviceAdapterListener deviceAdapterListener){
        deviceAdapterListenerArrayList.add(deviceAdapterListener);
    }

    private void sendListener(BluetoothDevice item, int position){
        for(int i = deviceAdapterListenerArrayList.size()-1 ; i >= 0 ; i--){
            deviceAdapterListenerArrayList.get(i).onClickDevice(item, position);
        }
    }

    @Override
    public int getCount() {
        return arrayDevice.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayDevice.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;
        if(convertView == null){
            layoutItem = (RelativeLayout)inflater.inflate(R.layout.adapter_select_base, parent, false);
        } else{
            layoutItem = (RelativeLayout)convertView;
        }
        TextView device_name = (TextView)layoutItem.findViewById(R.id.adapter_base_enceinte_nom);
        TextView device_address = (TextView)layoutItem.findViewById(R.id.adapter_base_enceinte_addresse);

        device_name.setText(arrayDevice.get(position).getName());
        device_address.setText(arrayDevice.get(position).getAddress());

        ImageButton selection = (ImageButton)layoutItem.findViewById(R.id.adapter_enceinte_base_button_ajouter);
        selection.setTag(position);
        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer position = (Integer) view.getTag();
                sendListener(arrayDevice.get(position),position);
            }
        });
        return layoutItem;
    }
}