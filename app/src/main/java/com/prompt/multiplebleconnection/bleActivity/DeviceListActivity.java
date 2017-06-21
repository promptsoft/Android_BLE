/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prompt.multiplebleconnection.bleActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.prompt.multiplebleconnection.R;
import com.prompt.multiplebleconnection.base.BaseActivity;
import com.prompt.multiplebleconnection.bleUtils.BluetoothLeService;
import com.prompt.multiplebleconnection.utils.Logger;
import com.prompt.multiplebleconnection.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends BaseActivity {

    public static final int CONNECTION_SUCCESS = 1;
    public static final int CONNECTION_FAILED = 2;

    private static final long SCAN_PERIOD_TIMEOUT = 5000;

    // Connection time out after 10 seconds.
    private static final long CONNECTION_TIMEOUT = 10000;

    // Activity request constant
    private static final int REQUEST_ENABLE_BT = 1;

    //Delay Time out
    private static final long DELAY_PERIOD = 500;
    private static final long SERVICE_DISCOVERY_TIMEOUT = 10000;
    private static final long ACTIVITY_FINISH_TIMEOUT = 30000;

    // device details
    public static String mDeviceName = "name";
    public static String mDeviceAddress = "address";

    //Bluetooth adapter
    private static BluetoothAdapter mBluetoothAdapter;
    // Devices list variables
    private static ArrayList<BluetoothDevice> mLeDevices;

    private Timer mScanTimer;
    private boolean mScanning;
    private Timer mConnectTimer;
    private boolean mConnectTimerON = false;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Map<String, Integer> mDevRssiValues;
    //GUI elements
    private ListView mProfileListView;
    private ProgressDialog mProgressdialog;
    //  Flags
    private boolean mSearchEnabled = false;
    private ProgressDialog mpdia;
    private AlertDialog mAlert;
    /**
     * Service Discovery
     */
    private Timer mTimer;
    private TextView txtNoDeviceFound;
    private Handler activityFinshHandler;
    private Runnable actvityFinishRunnable;
    private int cuurentDevice = REQUEST_CONNECT_DEVICE1;

    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            Activity mActivity = DeviceListActivity.this;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mSearchEnabled) {
                        mLeDeviceListAdapter.addDevice(device, rssi);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    };

    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server

            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    mProgressdialog.setMessage(getString(R.string.alert_message_bluetooth_connect));
                    if (mScanning) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mScanning = false;
                    }

                    if (mProgressdialog.isShowing() && !isActivityFinish())
                        mProgressdialog.dismiss();
                    mLeDevices.clear();
                    if (mConnectTimer != null)
                        mConnectTimer.cancel();
                    mConnectTimerON = false;
                    Toast.makeText(DeviceListActivity.this,
                            R.string.successfully_connected_to_the_device,
                            Toast.LENGTH_SHORT).show();

                    mLeDeviceListAdapter.notifyDataSetChanged();
                    mTimer = showServiceDiscoveryAlert(false);

                    startActivityFinishHandler();

                    //Changes the MTU size to 512 in case LOLLIPOP and above devices
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (cuurentDevice == REQUEST_CONNECT_DEVICE1)
                            BluetoothLeService.exchangeGattMtu1(512, mDeviceAddress);
                        else
                            BluetoothLeService.exchangeGattMtu2(512, mDeviceAddress);
                    }

                    break;


                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    /**
                     * Disconnect event.When the connectBLE1 timer is ON,Reconnect the device
                     * else show disconnect message
                     */
                    if (mConnectTimerON) {
                        if (cuurentDevice == REQUEST_CONNECT_DEVICE1)
                            BluetoothLeService.reconnectBLE1(mDeviceAddress);
                        else
                            BluetoothLeService.reconnectBLE2(mDeviceAddress);
                    } else {
                        Toast.makeText(DeviceListActivity.this,
                                R.string.profile_cannot_connect_message,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;


                case BluetoothLeService.ACTION_MTU_EXCHANGE:
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Logger.e("Discover service called");
                            if (cuurentDevice == REQUEST_CONNECT_DEVICE1)
                                BluetoothLeService.discoverServicesforBLE2();
                            else
                                BluetoothLeService.discoverServicesforBLE1();
                        }
                    }, DELAY_PERIOD);

                    break;
            }
        }
    };

    private final BroadcastReceiver mServiceDiscoveryListner = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    Logger.e("Service discovered");
                    if (mTimer != null)
                        mTimer.cancel();
                    if (mProgressdialog.isShowing() && !isActivityFinish())
                        mProgressdialog.dismiss();

                    sendConnectionStateToActivity(true);

                    break;
                case BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL:
                    if (mProgressdialog.isShowing() && !isActivityFinish())
                        mProgressdialog.dismiss();
                    if (mTimer != null)
                        mTimer.cancel();
                    showNoServiceDiscoverAlert();
                    scanLeDevice(true);
                    sendConnectionStateToActivity(false);
                    break;
                case BluetoothLeService.ACTION_MTU_EXCHANGE:
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Logger.e("Discover service called");
                            BluetoothLeService.discoverServicesforBLE2();

                        }
                    }, DELAY_PERIOD);
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        setContentView(R.layout.activity_device_list);
        this.setFinishOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDevRssiValues = new HashMap<String, Integer>();

        checkBleSupportAndInitialize();

        /**
         * Creating the dataLogger file and
         * updating the datalogger history
         */
        Logger.createDataLoggerFile(this);


        activityFinshHandler = new Handler();

        actvityFinishRunnable = new Runnable() {
            @Override
            public void run() {
                stopActivityFinishHandler();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!DeviceListActivity.this.isFinishing() || !DeviceListActivity.this.isDestroyed())
                        sendConnectionStateToActivity(false);
                }


            }
        };
        if (getIntent().hasExtra("CurrentDevice")) {
            cuurentDevice = getIntent().getIntExtra("CurrentDevice", REQUEST_CONNECT_DEVICE1);
        }
        initializeWidget();
        bindEvents();

    }

    /**
     * Initializes UI widgets
     */
    protected void initializeWidget() {

        txtNoDeviceFound = (TextView) findViewById(android.R.id.empty);
        mProfileListView = (ListView) findViewById(android.R.id.list);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mProfileListView.setAdapter(mLeDeviceListAdapter);
        mProfileListView.setTextFilterEnabled(true);
        mProgressdialog = new ProgressDialog(this);
        mProgressdialog.setCancelable(false);

        mpdia = new ProgressDialog(this);
        mpdia.setCancelable(false);
        mAlert = new AlertDialog.Builder(this).create();
        mAlert.setMessage(getResources().getString(
                R.string.alert_message_bluetooth_reconnect));
        mAlert.setCancelable(false);
        mAlert.setTitle(getResources().getString(R.string.app_name));
        mAlert.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(
                R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intentActivity = getIntent();
                finish();
                overridePendingTransition(
                        R.anim.slide_left, R.anim.push_left);
                startActivity(intentActivity);
                overridePendingTransition(
                        R.anim.slide_right, R.anim.push_right);
            }
        });
        mAlert.setCanceledOnTouchOutside(false);

        mProfileListView.setEmptyView(txtNoDeviceFound);

    }

    /**
     * Bind events to UI widgets
     */
    protected void bindEvents() {
        mProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mLeDeviceListAdapter.getCount() > 0) {
                    final BluetoothDevice device = mLeDeviceListAdapter
                            .getDevice(position);
                    if (device != null) {
                        scanLeDevice(false);
                        //TODO Device Coonect
                        connectDevice(device, true);
                    }
                }
            }
        });

    }

    @Override
    protected Context getContext() {
        return this;
    }


    @Override
    public void onResume() {
        Logger.e("Scanning onResume");
        if (checkBluetoothStatus()) {
            prepareList();
        }
        Logger.e("Registering receiver in Profile scannng");

        registerReceiver(mGattConnectReceiver, Utils.makeGattConnectIntentFilter());
        registerReceiver(mServiceDiscoveryListner, Utils.makeGattServiceDiscoveryIntentFilter());
//        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scanLeDevice(false);
        stopActivityFinishHandler();
        if (mLeDeviceListAdapter != null)
            mLeDeviceListAdapter.clear();
        if (mLeDeviceListAdapter != null) {
            try {
                mLeDeviceListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //UN register receivers
        unregisterReceiver(mGattConnectReceiver);
        unregisterReceiver(mServiceDiscoveryListner);

    }

    /**
     * start timer to finish activity in any case
     */
    private void startActivityFinishHandler() {
        if (activityFinshHandler != null && actvityFinishRunnable != null) {
            activityFinshHandler.removeCallbacks(actvityFinishRunnable);
            activityFinshHandler.postDelayed(actvityFinishRunnable, ACTIVITY_FINISH_TIMEOUT);
        }
    }
    /**
     * stop activity finish timer
     */
    private void stopActivityFinishHandler() {
        if (activityFinshHandler != null && actvityFinishRunnable != null)
            activityFinshHandler.removeCallbacks(actvityFinishRunnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable BlueTooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            // Check which request we're responding to
            if (requestCode == REQUEST_ENABLE_BT) {

                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(
                            DeviceListActivity.this,
                            getResources().getString(
                                    R.string.device_bluetooth_on),
                            Toast.LENGTH_SHORT).show();
                    mLeDeviceListAdapter = new LeDeviceListAdapter();
                    mProfileListView.setAdapter(mLeDeviceListAdapter);
                    prepareList();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Check device support Bluetooth or not
     */
    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(DeviceListActivity.this, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(DeviceListActivity.this,
                    R.string.device_bluetooth_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }

    /**
     * Check blue tooth status
     * @return : true if bluetooth on
     */
    public boolean checkBluetoothStatus() {
        /**
         * Ensures Blue tooth is enabled on the device. If Blue tooth is not
         * currently enabled, fire an intent to display a dialog asking the user
         * to grant permission to enable it.
         */
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    /**
     * Preparing the BLE Devicelist
     */
    public void prepareList() {
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mProfileListView.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
        mSearchEnabled = false;
    }


    /**
     * Method to scan BLE Devices. The status of the scan will be detected in
     * the BluetoothAdapter.LeScanCallback
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            if (!mScanning) {
                startScanTimer();
                mScanning = true;

                mBluetoothAdapter.startLeScan(mLeScanCallback);

            }
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    /**
     * Swipe refresh timer
     */
    public void startScanTimer() {
        mScanTimer = new Timer();
        mScanTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                scanLeDevice(false);
            }
        }, SCAN_PERIOD_TIMEOUT);
    }


    /**
     * Method to connectBLE1 to the device selected. The time allotted for having a
     * connection is 8 seconds. After 8 seconds it will disconnect if not
     * connected and initiate scan once more
     *
     * @param device
     */

    private void connectDevice(BluetoothDevice device, boolean isFirstConnect) {
        Logger.v("connectDevice Called...");

        mDeviceAddress = device.getAddress();
        mDeviceName = device.getName();
        // Get the connection status of the device
        if (BluetoothLeService.getmConnectionStateBLE1() == BluetoothLeService.STATE_DISCONNECTED) {
            Logger.v("BLE DISCONNECTED STATE");
            // Disconnected,so connectBLE1
            if (cuurentDevice == REQUEST_CONNECT_DEVICE1) {
                BluetoothLeService.connectBLE1(DeviceListActivity.this, mDeviceAddress, mDeviceName);
                showConnectAlertMessage(mDeviceName, mDeviceAddress);
            } else {
                BluetoothLeService.connectBLE2(DeviceListActivity.this, mDeviceAddress, mDeviceName);
                showConnectAlertMessage(mDeviceName, mDeviceAddress);
            }
        } else {
            Logger.v("BLE OTHER STATE-->" + BluetoothLeService.getmConnectionStateBLE1());
            // Connecting to some devices,so disconnect and then connectBLE1
            BluetoothLeService.disconnectBLE1();

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (cuurentDevice == REQUEST_CONNECT_DEVICE1) {
                        BluetoothLeService.connectBLE1(DeviceListActivity.this, mDeviceAddress, mDeviceName);
                        showConnectAlertMessage(mDeviceName, mDeviceAddress);
                    } else {
                        BluetoothLeService.connectBLE2(DeviceListActivity.this, mDeviceAddress, mDeviceName);
                        showConnectAlertMessage(mDeviceName, mDeviceAddress);
                    }
                }
            }, DELAY_PERIOD);

        }
        if (isFirstConnect) {
            startConnectTimer();
            mConnectTimerON = true;
        }

    }

    /**
     * show connectBLE1 alert dialog
     * @param devicename : name of current ble Device
     * @param deviceaddress :  address of current ble Device
     */
    private void showConnectAlertMessage(String devicename, String deviceaddress) {
        mProgressdialog.setTitle(getResources().getString(
                R.string.alert_message_connect_title));
        mProgressdialog.setMessage(getResources().getString(
                R.string.alert_message_connect)
                + "\n"
                + devicename
                + "\n"
                + deviceaddress
                + "\n"
                + getResources().getString(R.string.alert_message_wait));

        if (!DeviceListActivity.this.isDestroyed() && mProgressdialog != null) {
            mProgressdialog.show();
        }
    }

    /**
     * Connect Timer
     */
    private void startConnectTimer() {
        mConnectTimer = new Timer();
        mConnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mProgressdialog.isShowing() && !isActivityFinish())
                    mProgressdialog.dismiss();
                Logger.v("CONNECTION TIME OUT");
                mConnectTimerON = false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DeviceListActivity.this,
                                R.string.profile_cannot_connect_message,
                                Toast.LENGTH_SHORT).show();
                        if (mLeDeviceListAdapter != null)
                            mLeDeviceListAdapter.clear();
                        if (mLeDeviceListAdapter != null) {
                            try {
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        scanLeDevice(true);
                        mScanning = true;
                    }
                });

            }
        }, CONNECTION_TIMEOUT);
    }

    /**
     * Sevice Descovery aprogressbar dialog
     * @param isReconnect
     * @return
     */
    private Timer showServiceDiscoveryAlert(boolean isReconnect) {
        mProgressdialog.setTitle(getString(R.string.progress_tile_service_discovering));
        if (!isReconnect) {
            mProgressdialog.setMessage(getString(R.string.progress_message_service_discovering));
        } else {
            mProgressdialog.setMessage(getString(R.string.progress_message_reconnect));
        }
        mProgressdialog.setIndeterminate(true);
        mProgressdialog.setCancelable(false);
        mProgressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (mProgressdialog.isShowing() && !isActivityFinish())
            mProgressdialog.dismiss();

        mProgressdialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mProgressdialog.isShowing() && !isActivityFinish()) {
                    mProgressdialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNoServiceDiscoverAlert();
                            sendConnectionStateToActivity(false);
                        }
                    });
                }

            }
        }, SERVICE_DISCOVERY_TIMEOUT);
        return timer;
    }

    /**
     * Send response to caller activty
     * @param connectionState true if Device coonected,
     *                        false in any other case
     *
     */
    private void sendConnectionStateToActivity(boolean connectionState) {
        Intent intent = new Intent();
        if (connectionState) {
            intent.putExtra("ADDRESS", mDeviceAddress);
            setResult(CONNECTION_SUCCESS, intent);
        } else {
//            BluetoothLeService.disconnectBLE1();
            setResult(CONNECTION_FAILED, intent);
        }
        finish();
    }

    /**
     * Desiplay Toast if no service discovered
     */
    private void showNoServiceDiscoverAlert() {
        Toast.makeText(this, "No Service Discovered", Toast.LENGTH_SHORT).show();
    }

    /**
     * Holder class for the list view view widgets
     */
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }

    /**
     * List Adapter for holding devices found through scanning.
     */
    private class LeDeviceListAdapter extends BaseAdapter implements Filterable {

        ArrayList<BluetoothDevice> mFilteredDevices = new ArrayList<BluetoothDevice>();
        private LayoutInflater mInflator;
        private int rssiValue;
        private ItemFilter mFilter = new ItemFilter();

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        /**
         * Add device to list if it doesn't exist in the list
         * @param device Ddevice object
         * @param rssi rssi value of that Device (it is not used any where)
         */
        private void addDevice(BluetoothDevice device, int rssi) {
            this.rssiValue = rssi;
            // New device found
            if (!mLeDevices.contains(device)) {
                mDevRssiValues.put(device.getAddress(), rssi);
                mLeDevices.add(device);
            } else {
                mDevRssiValues.put(device.getAddress(), rssi);
            }
        }

        public int getRssiValue() {
            return rssiValue;
        }

        /**
         * Getter method to get the blue tooth device
         *
         * @param position
         * @return BluetoothDevice
         */
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        /**
         * Clearing all values in the device array list
         */
        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }


        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, viewGroup,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                viewHolder.deviceRssi = (TextView) view
                        .findViewById(R.id.device_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            /**
             * Setting the name and the RSSI of the BluetoothDevice. provided it
             * is a valid one
             */
            final BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                try {
                    viewHolder.deviceName.setText(deviceName);
                    viewHolder.deviceAddress.setText(device.getAddress());
                    byte rssival = (byte) mDevRssiValues.get(device.getAddress())
                            .intValue();
                    if (rssival != 0) {
                        viewHolder.deviceRssi.setText(String.valueOf(rssival));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                viewHolder.deviceName.setText(R.string.device_unknown);
                viewHolder.deviceName.setSelected(true);
                viewHolder.deviceAddress.setText(device.getAddress());
            }

            return view;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String mFilterString = constraint.toString().toLowerCase();

                FilterResults mResults = new FilterResults();

                final ArrayList<BluetoothDevice> list = mLeDevices;

                int count = list.size();
                final ArrayList<BluetoothDevice> nlist = new ArrayList<BluetoothDevice>(count);

                for (int i = 0; i < count; i++) {
                    if (list.get(i).getName() != null && list.get(i).getName().toLowerCase().contains(mFilterString)) {
                        nlist.add(list.get(i));
                    }
                }

                mResults.values = nlist;
                mResults.count = nlist.size();
                return mResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredDevices = (ArrayList<BluetoothDevice>) results.values;
                clear();
                int count = mFilteredDevices.size();
                for (int i = 0; i < count; i++) {
                    BluetoothDevice mDevice = mFilteredDevices.get(i);
                    mLeDeviceListAdapter.addDevice(mDevice, mLeDeviceListAdapter.getRssiValue());
                    notifyDataSetChanged(); // notifies the data with new filtered values
                }
            }
        }
    }

}
