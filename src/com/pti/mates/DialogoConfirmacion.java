package com.pti.mates;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class DialogoConfirmacion extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	 
	        AlertDialog.Builder builder =
	                new AlertDialog.Builder(getActivity());
	 
	        builder.setMessage("ÀQuiere activar el GPS?")
	        .setTitle("Activar el GPS")
	        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()  {
	               public void onClick(DialogInterface dialog, int id) {
	                    Log.i("Dialogos", "Confirmacion Aceptada.");
	                        dialog.cancel();
	                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                        startActivity(intent);
	                   }
	               })
	        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                        Log.i("Dialogos", "Confirmacion Cancelada.");
	                        dialog.cancel();
	                   }
	               });
	 
	        return builder.create();
	    }
}
	