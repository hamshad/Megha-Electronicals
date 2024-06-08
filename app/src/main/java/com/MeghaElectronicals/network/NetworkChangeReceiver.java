package com.MeghaElectronicals.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.databinding.InternetAlertdialogBinding;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static AlertDialog alertDialog;
    private static boolean isDialogShown = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean isConnected = NetworkUtil.isConnected(context);

            if (isConnected && !isDialogShown) {
                Log.e("TAG", "onReceive: TRUE");
                showAlertDialog(context);
                isDialogShown = true;
            }
            if (!isConnected && isDialogShown) {
                Log.e("TAG", "onReceive: FALSE");
                dismissAlertDialog();
                isDialogShown = false;
            }
        }
    }

    public void showAlertDialog (Context context) {
        if (alertDialog == null) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            InternetAlertdialogBinding view = InternetAlertdialogBinding.inflate(LayoutInflater.from(context));

            dialog.setView(view.getRoot());
            alertDialog = dialog.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
            view.connectMobileDataButton.setOnClickListener(v -> {
                Intent i = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                context.startActivity(i);
            });
            view.connectWifiButton.setOnClickListener(v -> {
                Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                context.startActivity(i);
            });

        }
        if (!((Activity) context).isFinishing())
            alertDialog.show();
    }

    public void dismissAlertDialog () {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
