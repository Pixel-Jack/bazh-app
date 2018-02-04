package fr.clement.rennsurrection.bluesound.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.clement.rennsurrection.bluesound.AddSpeaker.CoAdapterListener;
import fr.clement.rennsurrection.bluesound.Objects.Speaker;
import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Laure on 07/02/2017.
 */

public class ConnectionAdapter extends BaseAdapter {
    private List<Speaker> mListC;
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<CoAdapterListener> mListListener = new ArrayList<CoAdapterListener>();

    public ConnectionAdapter(Context context, List<Speaker> aListE) {
        mContext = context;
        mListC = aListE;
        mInflater = LayoutInflater.from(mContext);
    }

    public void addListener(CoAdapterListener aListener) {
        mListListener.add(aListener);
    }

    private void sendListener(Speaker item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClickName(item, position);
        }
    }


    public int getCount(){
        return mListC.size();
    }

    public Object getItem(int position){
        return mListC.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        RelativeLayout layoutItem;
        if (convertView==null){
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.adapter_list_speaker_main, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView speaker_co = (TextView) layoutItem.findViewById(R.id.adapter_main_speaker_name);
        speaker_co.setText(mListC.get(position).getNom());

        TextView speaker_add = (TextView) layoutItem.findViewById(R.id.adapter_main_speaker_address);
        speaker_add.setText(mListC.get(position).getAddress());

        TextView speaker_vol = (TextView) layoutItem.findViewById(R.id.adapter_main_speaker_Vol);
        speaker_vol.setText("Volume : " + mListC.get(position).getVolume() );

        ImageButton settings_speaker = (ImageButton)layoutItem.findViewById(R.id.adapter_speaker_main_button_settings);
        settings_speaker.setTag(position);
        settings_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                sendListener(mListC.get(position),position);
            }
        });
        return layoutItem;
    }
}