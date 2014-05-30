package com.pti.mates;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddContactDialog extends DialogFragment {
	private AlertDialog alertDialog;
	private EditText et;

	public static AddContactDialog newInstance() {
		AddContactDialog fragment = new AddContactDialog();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		et = new EditText(getActivity());
		et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		et.setHint("abc@example.com");
		alertDialog = new AlertDialog.Builder(getActivity())
		.setTitle("Add Contact").setMessage("Add Contact")
		.setPositiveButton(android.R.string.ok, null)
		.setNegativeButton(android.R.string.cancel, null)
		.setView(et).create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button okBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String fbid= et.getText().toString();
						/*if (!isEmailValid(email)) {
							et.setError("Invalid email!");
							return;
						}*/
						try {
							ContentValues values = new ContentValues(2);
							values.put(DataProvider.COL_NAME, "XAVI"); 
							/**
							 * quan es faci mate el nom mel donaran. s'ha de modificar el receiver
							 * per diferenciar quan m'han enviat un missatge de quan es fa mate 
							 * per part de les dues persones afegir la persona a la bd amb el nom i
							 * fbid que em donaran.
							*/
							values.put(DataProvider.COL_FBID, fbid);
							getActivity().getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values);
						} catch (SQLException sqle) {}
						alertDialog.dismiss();
					}
				});
			}
		});
		return alertDialog;
	}
}