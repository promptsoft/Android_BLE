package com.prompt.multiplebleconnection.bleActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prompt.multiplebleconnection.R;
import com.prompt.multiplebleconnection.base.BaseActivity;
import com.prompt.multiplebleconnection.bleUtils.BluetoothLeService;
import com.prompt.multiplebleconnection.bleUtils.OnDataReceiveInterface;
import com.prompt.multiplebleconnection.utils.Utils;

public class MainActivity extends BaseActivity implements View.OnClickListener, OnDataReceiveInterface {
    private Intent gattServiceIntent;
    private TextView txtDataFromBLE1, txtDataFromBLE2;
    private EditText edtDataBLE1,edtDataBLE2 ;
    private Button btnConfigureBLE1, btnSendDataBLE1, btnConfigureBLE2, btnSendDataBLE2;
    private String bleAddress1, bleAddress2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtDataFromBLE1 = (TextView) findViewById(R.id.txtDataFromBLE1);
        txtDataFromBLE2 = (TextView) findViewById(R.id.txtDataFromBLE2);


        btnConfigureBLE1 = (Button) findViewById(R.id.btnConfigureBLE1);
        btnConfigureBLE2 = (Button) findViewById(R.id.btnConfigureBLE2);
        btnSendDataBLE1 = (Button) findViewById(R.id.btnSendDataBLE1);
        btnSendDataBLE2 = (Button) findViewById(R.id.btnSendDataBLE2);

        edtDataBLE1 = (EditText) findViewById(R.id.edtDataBLE1);
        edtDataBLE2 = (EditText) findViewById(R.id.edtDataBLE2);

        btnConfigureBLE1.setOnClickListener(this);
        btnConfigureBLE2.setOnClickListener(this);
        btnSendDataBLE1.setOnClickListener(this);
        btnSendDataBLE2.setOnClickListener(this);

        gattServiceIntent = new Intent(getApplicationContext(),
                BluetoothLeService.class);
        gattServiceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        gattServiceIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startService(gattServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dataReceiverBle, Utils.makeGattUpdateIntentFilter());
        registerReceiver(mGattConnectReceiver, Utils.makeGattConnectIntentFilter());
    }

    @Override
    protected Context getContext() {
        return this;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, DeviceListActivity.class);
        switch (v.getId()) {
            case R.id.btnConfigureBLE1:
                if(btnConfigureBLE1.getText().toString().equals(res.getString(R.string.disconnect))){
                    btnConfigureBLE1.setText(res.getString(R.string.connect));
                    txtDataFromBLE1.setText("No Device Connected");
                    BluetoothLeService.disconnectBLE1();
                }else{
                    intent.putExtra("CurrentDevice", REQUEST_CONNECT_DEVICE1);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE1);
                }

                break;
            case R.id.btnConfigureBLE2:
                if(btnConfigureBLE1.getText().toString().equals(res.getString(R.string.disconnect))){
                    btnConfigureBLE2.setText(res.getString(R.string.connect));
                    txtDataFromBLE2.setText("No Device Connected");
                    BluetoothLeService.disconnectBLE2();
                }else{
                    intent.putExtra("CurrentDevice", REQUEST_CONNECT_DEVICE2);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE2);
                }
                break;
            case R.id.btnSendDataBLE1:
                BluetoothLeService.writeCharacteristicGattBLE1(edtDataBLE1.getText().toString().trim().getBytes());
                break;
            case R.id.btnSendDataBLE2:
                BluetoothLeService.writeCharacteristicGattBLE2(edtDataBLE2.getText().toString().trim().getBytes());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONNECT_DEVICE1) {
            switch (resultCode) {
                case DeviceListActivity.CONNECTION_SUCCESS:
                    // When DeviceListActivity returns with a device to connectBLE1
                    if (data.hasExtra("ADDRESS")) {
                        bleAddress1 = data.getStringExtra("ADDRESS");
                        txtDataFromBLE1.setText(bleAddress1 + " : Please Send some data");
                        btnConfigureBLE1.setText(res.getString(R.string.disconnect));
                    }
                    break;
                case DeviceListActivity.CONNECTION_FAILED:
                    Toast.makeText(this, R.string.ble_not_connected,
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        } else if (requestCode == REQUEST_CONNECT_DEVICE2) {
            switch (resultCode) {
                case DeviceListActivity.CONNECTION_SUCCESS:
                    // When DeviceListActivity returns with a device to connectBLE1
                    if (data.hasExtra("ADDRESS")) {
                        bleAddress2 = data.getStringExtra("ADDRESS");
                        txtDataFromBLE2.setText(bleAddress2 + " : Please Send some data");
                        btnConfigureBLE2.setText(res.getString(R.string.disconnect));
                    }
                    break;
                case DeviceListActivity.CONNECTION_FAILED:
                    Toast.makeText(this, R.string.ble_not_connected,
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    }

    @Override
    public void onDataReceived(String deviceAddress, String data) {

        if (deviceAddress.equals(bleAddress1))
            txtDataFromBLE1.setText(deviceAddress + " : " + data);
        else if (deviceAddress.equals(bleAddress2))
            txtDataFromBLE2.setText(deviceAddress + " : " + data);
        else
            Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dataReceiverBle);
        unregisterReceiver(mGattConnectReceiver);
    }
}
