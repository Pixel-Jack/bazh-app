package fr.clement.rennsurrection.bluesound.AddSpeaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import fr.clement.rennsurrection.bluesound.R;

/**
 * Created by Clément on 20/02/2017.
 */

public class DialogPair extends DialogFragment {
    private String title = "Pairing";

    public interface DialogPairingListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DialogPairingListener mListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        //Verify that the host activity implements the callback interface
        try {
            //Instantiate the DialogPairingListener so we can send events to the host
            mListener = (DialogPairingListener) activity;
        } catch (ClassCastException e){
            //The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement DialogPairingListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Inflate and set the layout for the dialog
        //Pass null as the parent view because its going in the dialog layout
        builder.setMessage(this.title);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_info,getContext().getTheme()));
        builder.setView(inflater.inflate(R.layout.adapter_dialogue_waiting_pair, null));

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Send the nagative button event back to the host acivtiy
                mListener.onDialogNegativeClick(DialogPair.this);
            }
        });
        return builder.create();
    }
}