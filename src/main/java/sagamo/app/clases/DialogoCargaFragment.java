package sagamo.app.clases;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import sagamo.app.R;

public class DialogoCargaFragment {


    Fragment activity;
    AlertDialog dialog;

    public DialogoCargaFragment(Fragment myActivity){
        activity = myActivity;
    }

    public void startProgressAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.sag_loading, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    public void stopProgressAlertDialog(){
        dialog.dismiss();
    }

}
