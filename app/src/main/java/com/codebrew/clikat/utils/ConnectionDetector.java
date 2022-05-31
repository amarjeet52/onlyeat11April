package com.codebrew.clikat.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

import com.codebrew.clikat.R;


public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();

            return activeNetwork != null
                    && activeNetwork.isConnected();
        }
        return false;
    }

    public void showNoInternetDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
        alertDialog.setTitle(_context.getResources().getString(R.string.app_name));
        alertDialog.setCancelable(false);
        alertDialog.setMessage(_context.getResources().getString(R.string.internet_connection));
        alertDialog.setPositiveButton(_context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
