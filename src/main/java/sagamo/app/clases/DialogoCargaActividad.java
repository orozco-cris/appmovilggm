package sagamo.app.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import sagamo.app.R;

public class DialogoCargaActividad {
    Activity activity;
    AlertDialog dialog;

    public DialogoCargaActividad(Activity myActivity){
        activity = myActivity;
    }

    public void startProgressAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
