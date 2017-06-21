package com.prompt.multiplebledeviceconnection.base;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.prompt.multiplebledeviceconnection.R;
import com.prompt.multiplebledeviceconnection.bleUtils.BluetoothLeService;
import com.prompt.multiplebledeviceconnection.bleUtils.GattConnectReceiver;
import com.prompt.multiplebledeviceconnection.broadcastReceivers.DataReceiverBle;
import com.prompt.multiplebledeviceconnection.uicomponents.MyAlertDialog;
import com.prompt.multiplebledeviceconnection.utils.Const;
import com.prompt.multiplebledeviceconnection.utils.CustomExceptionHandler;
import com.prompt.multiplebledeviceconnection.utils.Prefs;
import com.prompt.multiplebledeviceconnection.utils.Utils;

import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * Created by prompt on 30/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {


    public static HashMap<String, String> SETTINGS = new HashMap<>();


    //Inten request code for get BLE address
    protected final int BLE1 = 1;
    protected final int BLE2 = 2;
    protected final int BLE3 = 3;
    protected final int BLE4 = 4;
    protected final int BLE_HW = 5;

    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_BLE_CONNECT = 3;

    //TODO PRINT FORMATES
    public static final java.text.NumberFormat NO3 = new java.text.DecimalFormat("000");
    public static final java.text.NumberFormat NO4 = new java.text.DecimalFormat("0000");
    public static final java.text.NumberFormat NO5 = new java.text.DecimalFormat("00000");
    public static final java.text.NumberFormat PT2 = new java.text.DecimalFormat("#.00");
    public static final java.text.NumberFormat PT1 = new java.text.DecimalFormat("#0.0");

    protected Intent intent;
    protected MyAlertDialog alertDialog;
    protected Resources res;
    protected ProgressDialog progressDialog;
    protected String TAG = "BASE ACTIVITY";
    protected AlertDialog.Builder selectionDialog;
    protected Dialog dialog;
    protected Handler handler = new Handler();
    private Context context;
    private TextView txtMessage;
    private Button btnYes, btnNo;
    private int ID = 1212;

    protected DataReceiverBle dataReceiverBle;
    protected GattConnectReceiver mGattConnectReceiver;

    /**
     * Used to manage connections of the Blue tooth LE Device
     */
    private static BluetoothLeService mBluetoothLeService;

    public BaseActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        res = getContext().getResources();

        if (dataReceiverBle == null)
            dataReceiverBle = DataReceiverBle.getInstance(context);
        if (mGattConnectReceiver == null)
            mGattConnectReceiver = GattConnectReceiver.getInstance(context);

        progressDialog = new ProgressDialog(this.context);

        progressDialog.setMessage(res.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        TAG = this.context.getClass().getName();

        alertDialog = new MyAlertDialog(getContext());
        Const.USER_ROLE = Utils.getInt(Prefs.getvalue(this, Const.PREF_USER_ROLE, "6"));


        selectionDialog = new AlertDialog.Builder(getContext());
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_selection);
        dialog.setCancelable(false);


        txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        btnYes = (Button) dialog.findViewById(R.id.btnYes);
        btnNo = (Button) dialog.findViewById(R.id.btnNo);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnYes.setEnabled(false);
                btnNo.setEnabled(false);
                dialogResponse(ID, true);
                if (!isActivityFinish())
                    dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnYes.setEnabled(false);
                btnNo.setEnabled(false);
                dialogResponse(ID, false);
                if (!isActivityFinish())
                    dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                btnYes.setEnabled(true);
                btnNo.setEnabled(true);
            }
        });

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            String path = Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name);
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(path, ""));
        }

    }

    public boolean isActivityFinish() {
        return BaseActivity.this.isFinishing() || BaseActivity.this.isDestroyed();
    }


    @Override
    protected void onStart() {
        super.onStart();

        initializeWidget();
        bindEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract void initializeWidget();

    protected abstract void bindEvents();


    protected abstract Context getContext();


    protected void showProgressDialog() {
        if (!progressDialog.isShowing() && !isActivityFinish())
            progressDialog.show();
    }

    protected void showProgressDialog(String message) {
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }


    protected void showProgressDialog(String title, String message) {
        if (!progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    protected void hideProgressDialog() {
        if (progressDialog.isShowing() && !isActivityFinish())
            progressDialog.hide();
    }

    protected void showAlert(String message) {
        alertDialog.setIcon(R.drawable.ic_alert);
        alertDialog.setMessage(message);
        alertDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing() && !isActivityFinish()) {

                    alertDialog.dismiss();
                }
            }
        }, Const.DIALOG_DISPLAY_TIME);
    }

    protected void showAlert(int id, String message) {
        this.ID = id;
        alertDialog.setIcon(R.drawable.ic_alert);
        alertDialog.setMessage(message);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogResponse(ID, true);
            }
        });
        alertDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing() && !isActivityFinish())
                    alertDialog.dismiss();
            }
        }, Const.DIALOG_DISPLAY_TIME);
    }

    protected void showSuccess(String message) {
        alertDialog.setIcon(R.drawable.ic_success);
        alertDialog.setMessage(message);
            alertDialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isActivityFinish() && alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        }, Const.DIALOG_DISPLAY_TIME);
    }


    protected void showSuccess(int id, String message) {
        alertDialog.setIcon(R.drawable.ic_success);
        alertDialog.setMessage(message);
        this.ID = id;
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogResponse(ID, true);
            }
        });
        alertDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing() && !isActivityFinish())
                    alertDialog.dismiss();
            }
        }, Const.DIALOG_DISPLAY_TIME);
    }

    protected abstract void dialogResponse(int id, boolean res);

    protected void showSelectionDialog(final int id, String message) {
        //selectionDialog.setIcon(icon);
        txtMessage.setText(message);
        this.ID = id;
        dialog.show();
    }

    protected void hideSelectionDialog(final int id, String message) {
        if(!isActivityFinish() && dialog.isShowing()){
            dialog.dismiss();
        }
    }


}
