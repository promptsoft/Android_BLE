package com.prompt.multiplebleconnection.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;


import com.prompt.multiplebleconnection.R;
import com.prompt.multiplebleconnection.broadcastReceivers.GattConnectReceiver;
import com.prompt.multiplebleconnection.broadcastReceivers.DataReceiverBle;
import com.prompt.multiplebleconnection.uicomponents.MyAlertDialog;
import com.prompt.multiplebleconnection.utils.CustomExceptionHandler;


/**
 * Created by prompt on 30/1/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_CONNECT_DEVICE1 = 1;
    public static final int REQUEST_CONNECT_DEVICE2 = 2;

    protected MyAlertDialog alertDialog;
    protected Resources res;
    protected ProgressDialog progressDialog;
    protected String TAG = "BASE ACTIVITY";
    protected Handler handler = new Handler();
    private Context context;
    private TextView txtMessage;
    private Button btnYes, btnNo;
    private int ID = 1212;

    protected DataReceiverBle dataReceiverBle;
    protected GattConnectReceiver mGattConnectReceiver;

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

    /**
     * get Context of current activity
     * @return
     */
    protected abstract Context getContext();

//    /**
//     * Send selection dialog response to current activty
//     * @param id dialog ID
//     * @param res reposnse
//     */
//    protected abstract void dialogResponse(int id, boolean res);

}
