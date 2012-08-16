package com.csc780.pacmon;

import com.csc780.clientmultiserver.CMGameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ClientOrServer extends Activity {
	
	private static final int SERVER_CLIENT = 0;
	private ProgressDialog mProgressDialog;
    private Handler mProgressHandler, mProgressHandler2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clientorserver);
		
		this.showDialog(0);
		
	}
	
	
	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case SERVER_CLIENT:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Choose to be Server or Client...").setCancelable(false)
    				.setPositiveButton("Server", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						Intent cmGame = new Intent("com.csc780.clientmultiserver.SERVERSTARTED");
    						startActivityForResult(cmGame,0);
    					}
    				})
    				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						ClientOrServer.this.finish();
    						
    					}
    				})
    				.setNeutralButton("Client", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						Intent mGame = new Intent("com.csc780.multipacmon.SEARCHING");
    						startActivityForResult(mGame,0);
    					}
    				});
    		AlertDialog alert = builder.create();
    		alert.show();
    	return alert;
        
        default:
            return null;
        }
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.finish();

    }
}
