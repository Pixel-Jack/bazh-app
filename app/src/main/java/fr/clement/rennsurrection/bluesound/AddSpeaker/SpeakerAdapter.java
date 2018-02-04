package fr.clement.rennsurrection.bluesound.AddSpeaker;

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

import fr.clement.rennsurrection.bluesound.Objects.Speaker;
import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Laure on 06/02/2017.
 */

public class SpeakerAdapter extends BaseAdapter {
    private List<Speaker> mListE;
    private Context mContext;
    private LayoutInflater mInflater;

    public SpeakerAdapter(Context context, List<Speaker> aListE) {
        mContext = context;
        mListE = aListE;
        mInflater = LayoutInflater.from(mContext);
    }

    private ArrayList<SpeakerAdapterListener> mListListener = new ArrayList<SpeakerAdapterListener>();

    public void addListener(SpeakerAdapterListener aListener) {
        mListListener.add(aListener);
    }

    public int getCount(){
        return mListE.size();
    }

    public void setmListE(List<Speaker> aListE){
        mListE = aListE;
    }
    public Object getItem(int position){
        return mListE.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    private void sendListener(Speaker item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClickName(item, position);
        }
    }

    public View getView(final int position, View convertView, final ViewGroup parent){
        RelativeLayout layoutItem;
        if (convertView==null){
            layoutItem = (RelativeLayout) mInflater.inflate(R.layout.adapter_add_speaker, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView nameSpeaker = (TextView) layoutItem.findViewById(R.id.adapter_speaker_name);
        ImageButton aj = (ImageButton) layoutItem.findViewById(R.id.adapter_speaker_buton_add);
        TextView addresseEnceinte = (TextView)layoutItem.findViewById(R.id.adapter_speaker_address);

        aj.setTag(position);
        aj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                sendListener(mListE.get(position),position);
            }
        });

        nameSpeaker.setText(mListE.get(position).getNom());
        addresseEnceinte.setText(mListE.get(position).getAddress());

        return layoutItem;
    }
}