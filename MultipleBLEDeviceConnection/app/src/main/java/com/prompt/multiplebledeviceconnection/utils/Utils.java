package com.prompt.multiplebledeviceconnection.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.prompt.multiplebledeviceconnection.R;
import com.prompt.multiplebledeviceconnection.bleUtils.BluetoothLeService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Utils {
    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @purpose: It will return the date in yyyy-MM-dd format
     */
    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: give the format
     * @purpose: It will return the date in parameter format
     */
    public static String getDate(String dateformat) {
        try {
            return new SimpleDateFormat(dateformat).format(new Date());
        } catch (Exception e) {
            return "date not found";
        }
    }

    public static String getDate(String date, String fromDateFormate, String toDateFormate) {
        DateFormat fromDate = new SimpleDateFormat(fromDateFormate);
        DateFormat toDate = new SimpleDateFormat(toDateFormate);
        Date mDate = null;
        try {
            mDate = fromDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return Utils.getDate(toDateFormate);

        } catch (NullPointerException e) {
            e.printStackTrace();
            return Utils.getDate(toDateFormate);
        }
        return toDate.format(mDate);
    }

    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: void
     * @purpose: It will return the DateTime in dd-MM-yyyy HH:mm:ss format
     */
    public static String getDateTime() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: void
     * @purpose: It will return the DateTime in dd-MM-yyyy HH-mm-ss format
     */
    public static String getTimestemp() {
        return new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date());
    }

    public static String getDefaultTimeStemp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: Context and message
     * @purpose: It will show toast with long time
     */
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: Context and message
     * @purpose: It will show toast with short time
     */
    public static void showToastShort(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }



    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: Context
     * @purpose: It will give return Application Package Name.
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * @return String
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: Context
     * @purpose: return backup file name with timestamp
     */
    public static String getBackupFileName() {
        String path = "backup_amcs_" + Utils.getDate("ddMMyyHHmmss") + ".db";
        // Log.e("Utils Backup Path",path) ;
        return path;
    }

    public static String getLogFileName() {
        String path = "AMCS_" + Utils.getDate("ddMMyyHHmmss") + ".txt";
        // Log.e("Utils Backup Path",path) ;
        return path;
    }


    /**
     * @return String
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: int year,int month,int day
     * @purpose: It will convert date into String and dd-MM-yyyy formate
     */
    public static String formatDate(int year, int month, int day, String dateformat) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        return sdf.format(date);
    }

    /**
     * @return void.
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: Activity
     * @purpose: Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    Const.PERMISSIONS_STORAGE,
                    Const.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * @return int
     * @CreatedBy: Hiren Vaghela
     * @CreatedOn: 5/2/16
     * @param: void
     * @purpose: get the device api level
     */

    public static int getAPILevel() {
        return Build.VERSION.SDK_INT;
    }


    public static String getVersionCode(Context context) {
        String app_ver = "sdf";
        try {
            app_ver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode + ".0";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Exception", e.toString());
        }
        return app_ver;
    }


    public static Drawable resizeIcon(Context context, int drawable, int newWidth, int newHeight) {
        // load the origial BitMap (500 x 500 px)
        Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(), drawable);


        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        // calculate the scale - in this case = 0.4f
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Log.e("resizeIcon", "width=" + width + " height=" + height + " newWidth" + newWidth + " newHeight" + newHeight);

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap

        //  matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                width, height, matrix, true);

        // make a Drawable from Bitmap to allow to set the BitMap
        // to the ImageView, ImageButton or what ever
        return new BitmapDrawable(resizedBitmap);
    }


    public static String getDeviceIP(Context context) {
//        WifiManager wifiMgr = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
//        return Formatter.formatIpAddress(wifiInfo.getIpAddress());


        ArrayList<String> addresses = new ArrayList<String>();
        String str = "0.0.0.0";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                int i = 0;
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        str = inetAddress.getHostAddress();
                        //    addresses.add(inetAddress.getHostAddress().toString());
                        Log.e("IP Address" + i, inetAddress.getHostAddress());
                        //    i++;
                    }
                }
            }
        } catch (SocketException ex) {
            String LOG_TAG = null;
            Log.e(LOG_TAG, ex.toString());
        }
        Log.e("DEVICE IP", str);
        return str;//addresses.get(0);

    }


    public static String getGuid() {
        return String.valueOf(UUID.randomUUID());
    }





    public static boolean isValidDate(String input) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (null == input) {
            return false;
        }
        try {
            simpleDateFormat.setLenient(false);
            date = simpleDateFormat.parse(input);
        } catch (ParseException e) {

        }
        return date != null;

    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager objInputMethodManager = (InputMethodManager) activity
                .getSystemService(Service.INPUT_METHOD_SERVICE);
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showSoftKeyboard(EditText editText, Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static double getDouble(String str) {
        if (str == null || str.isEmpty())
            return 0.0;
        else{
            try {
                return Double.parseDouble(str);
            }catch (NumberFormatException e){
                return 0.0 ;
            }
        }
    }

    public static float getFloat(String str) {
        if (str == null || str.isEmpty())
            return 0.0f;
        else{
            try {
                return Float.parseFloat(str);
            }catch (NumberFormatException e){
                return 0.0f;
            }
        }

    }

    public static float getRoundFloat(float f) {
        return (float) (Math.round(f * 10.0) / 10.0);
    }

    public static float getRoundFloat(String f) {
        float temp = getFloat(f);
        return (float) (Math.round(temp * 10.0) / 10.0);
    }

    public static int getInt(String str) {
        if (str == null || str.isEmpty())
            return 0;
        else{
            try{
                return Integer.parseInt(str);
            }catch (NumberFormatException e){
                return 0 ;
            }
        }
    }

    public static int getInt(Double d) {
        return d.intValue();
    }

    public static void saveLogcatToFile(Context context, String activityName) {
        String fileName = "logcat_" + activityName + getDateTime() + ".txt";
        File outputFile = new File(context.getExternalCacheDir(), fileName);
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec("logcat -f " + outputFile.getAbsolutePath());
            //Process process = Runtime.getRuntime().exec("logcat -f "+outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

    }

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

//    public static ArrayList<String> chunk_split(String original, int length, boolean isUsb) throws IOException {
//        int start = 0, curr = length, index = 0;
////        for (index = 0; index < getInt(BaseActivity.SETTINGS.get(BaseActivity.NoOfBlankLines)); index++) {
////            original += BaseActivity.NEW_LINE;
////        }
//
//        int count = 0;
//        for (index = 0; index < original.length() && countLines(original) > 60; index++) {
//            if (original.charAt(index) == '\n' && count >= 60) {
//                count = 0;
//                String end = original.substring(index + 1);
//                original = original.substring(0, index) + Const.PRINTER_FORM_FEED + end;
//
//            } else if (original.charAt(index) == '\n') {
//                count++;
//            }
//        }
//
//        int data_length = original.length();
//        ArrayList<String> full_buffer = new ArrayList<String>();
//
//        while ((start + 1) < data_length) {
//            if (curr > data_length) {
//                curr = data_length - 1;
//                full_buffer.add(original.substring(start, curr));
//            } else {
//                String temp = original.substring(start, curr);
//                int lastOccureIndex = temp.lastIndexOf(BaseActivity.NEW_LINE);
//                if (temp.contains(Const.PRINTER_FORM_FEED)) {
//                    lastOccureIndex = temp.lastIndexOf(Const.PRINTER_FORM_FEED);
//                    lastOccureIndex++;
//                }
//                curr = start + lastOccureIndex;
//                full_buffer.add(original.substring(start, curr));
//            }
//            start = curr;
//            curr += length;
//        }
//
//        return full_buffer;
//    }



    public static String getDashedLine(int charPrLine) {
        //21 for big font printer
        //31 for small font printer
        String str = "";
        for (int i = 0; i < charPrLine; i++)
            str += "-";
        return str;
    }

    public static String getSpaceChar(int noOfSpace) {
        String str = "";
        for (int i = 0; i < noOfSpace; i++)
            str += " ";
        return str;
    }

    public static void clearError(EditText edt) {
        if (!edt.getText().toString().isEmpty() && edt.getError() != null) {
            edt.setError(null);
        }
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




    public static String getLanguageParse(String str) {
        switch (str) {
            case "1":
                return "en";
            case "2":
                return "hi";
            case "3":
                return "gu";
            default:
                return str;
        }
    }


    public static String getLanguageSlipParse(String str) {
        switch (str) {
            case "0":
                return "en";
            case "1":
                return "gu";
            case "2":
                return "hi";
            default:
                return "en";
        }
    }


    public static String convertToUnicode(String str, String printSlipLanguage) {

        StringBuilder retStr = new StringBuilder();
        if (printSlipLanguage.equalsIgnoreCase("1")) {
            for (int i = 0; i < str.length(); i++) {

                //TODO Selected Language is GUJARATI
                int cp = Character.codePointAt(str, i);
                int charCount = Character.charCount(cp);
                if (charCount > 1) {
                    i += charCount - 1; // 2.
                    if (i >= str.length()) {
                        throw new IllegalArgumentException("truncated unexpectedly");
                    }
                }

                if (cp == ' ')
                    retStr.append("20");
                else if (cp == '.')
                    retStr.append("0AE4");
                else if (cp == ':')
                    retStr.append("0AF0");
                else if (cp == '-')
                    retStr.append("0AE1");
                else if (cp == '(')
                    retStr.append("0ADE");
                else if (cp == ')')
                    retStr.append("0ADF");
                else if (cp == '\n')
                    retStr.append("0d");
                else
                    retStr.append(String.format("0%x", cp));
            }
            retStr.append("0d");
        } else if (printSlipLanguage.equalsIgnoreCase("2")) {
            //TODO Selected Language is HINDI
            for (int i = 0; i < str.length(); i++) {
                int cp = Character.codePointAt(str, i);
                int charCount = Character.charCount(cp);
                if (charCount > 1) {
                    i += charCount - 1; // 2.
                    if (i >= str.length()) {
                        throw new IllegalArgumentException("truncated unexpectedly");
                    }
                }
                if (cp == ' ')
                    retStr.append("20");
                else if (cp == '.')
                    retStr.append("0978");
                else if (cp == ':')
                    retStr.append("0903");
                else if (cp == '-')
                    retStr.append("0970");
                else if (cp == '(')
                    retStr.append("097E");
                else if (cp == ')')
                    retStr.append("097F");
                else if (cp == '\n')
                    retStr.append("0d0a");
                else
                    retStr.append(String.format("0%x", cp));
            }
            retStr.append("0d0a");
        }

        return retStr.toString();
    }

    public static String convertToUnicodeAddBlankLine(String str, String printSlipLanguage, String moreLine) {

        StringBuilder retStr = new StringBuilder();
        int mMoreLine = getInt(moreLine);
        if (printSlipLanguage.equalsIgnoreCase("1")) {
            for (int i = 0; i < str.length(); i++) {

                //TODO Selected Language is GUJARATI
                int cp = Character.codePointAt(str, i);
                int charCount = Character.charCount(cp);
                if (charCount > 1) {
                    i += charCount - 1; // 2.
                    if (i >= str.length()) {
                        throw new IllegalArgumentException("truncated unexpectedly");
                    }
                }

                if (cp == ' ')
                    retStr.append("20");
                else if (cp == '.')
                    retStr.append("0AE4");
                else if (cp == ':')
                    retStr.append("0AF0");
                else if (cp == '-')
                    retStr.append("0AE1");
                else if (cp == '(')
                    retStr.append("0ADE");
                else if (cp == ')')
                    retStr.append("0ADF");
                    /*else if (cp == '\r')
                        Log.e("Test","Test") ;*/
                else if (cp == '\n')
                    retStr.append("0d");
                else
                    retStr.append(String.format("0%x", cp));
            }
            retStr.append("0d");
        } else if (printSlipLanguage.equalsIgnoreCase("2")) {
            //TODO Selected Language is HINDI
            for (int i = 0; i < str.length(); i++) {
                int cp = Character.codePointAt(str, i);
                int charCount = Character.charCount(cp);
                if (charCount > 1) {
                    i += charCount - 1; // 2.
                    if (i >= str.length()) {
                        throw new IllegalArgumentException("truncated unexpectedly");
                    }
                }
                if (cp == ' ')
                    retStr.append("20");
                else if (cp == '.')
                    retStr.append("0978");
                else if (cp == ':')
                    retStr.append("0903");
                else if (cp == '-')
                    retStr.append("0970");
                else if (cp == '(')
                    retStr.append("097E");
                else if (cp == ')')
                    retStr.append("097F");
                else if (cp == '\n')
                    retStr.append("0d0a");
                else
                    retStr.append(String.format("0%x", cp));
            }
            retStr.append("0d0a");
            for (int count = 0; count < mMoreLine; count++) {
                retStr.append("0d0a");
            }
        }
       /* int mMoreLine = getInt(moreLine);
        for (int count = 0; count < mMoreLine; count++) {
            retStr.append("0d0a");
        }*/
        return retStr.toString();
    }


    public static boolean setSsidAndPassword(Context context, String ssid, String ssidPassword) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            wifiConfig.SSID = ssid;
            wifiConfig.preSharedKey = ssidPassword;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adding the necessary INtent filters for Broadcast receivers
     *
     * @return {@link IntentFilter}
     */

    public static IntentFilter makeGattConnectIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_MTU_EXCHANGE);
        return intentFilter;
    }

    public static IntentFilter makeGattServiceDiscoveryIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
        return intentFilter;
    }


    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static String ByteArraytoHex(byte[] bytes) {
        if (bytes != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString();
        }
        return "";
    }

    // Shared preference constant
    private static final String SHARED_PREF_NAME = "CySmart Shared Preference";
    private static ProgressDialog mProgressDialog;
    private static Timer mTimer;


    /**
     * Get the date
     *
     * @return {@link String}
     */
    public static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }


    /**
     * Get the seven days before date
     *
     * @return {@link String}
     */

    public static String GetDateSevenDaysBack() {
        DateFormat formatter = new SimpleDateFormat("dd_MMM_yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        return formatter.format(calendar.getTime());

    }

    /**
     * Get the time from milliseconds
     *
     * @return {@link String}
     */
    public static String GetTimeFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("HH:mm ss SSS");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get time and date
     *
     * @return {@link String}
     */

    public static String GetTimeandDate() {
        DateFormat formatter = new SimpleDateFormat("[dd-MMM-yyyy|HH:mm:ss]");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }


    public static boolean getBooleanSharedPreference(Context context,
                                                     String key) {
        boolean value;
        SharedPreferences Preference = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);
        value = Preference.getBoolean(key, false);
        return value;
    }

    public static void bondingProgressDialog(final Activity context, ProgressDialog pDialog,
                                             boolean status) {
        mProgressDialog = pDialog;
        if (status) {
            mProgressDialog.setTitle(context.getResources().getString(
                    R.string.alert_message_bonding_title));
            mProgressDialog.setMessage((context.getResources().getString(
                    R.string.alert_message_bonding_message)));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mTimer = setDialogTimer();

        } else {
            mProgressDialog.dismiss();
        }

    }

    public static Timer setDialogTimer() {
        Logger.e("Started Timer");
        long delayInMillis = 20000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        }, delayInMillis);
        return timer;
    }

    public static void stopDialogTimer() {
        if (mTimer != null) {
            Logger.e("Stopped Timer");
            mTimer.cancel();
        }
    }

    public static String getBleAddress(BluetoothDevice device) {
        return device.getAddress().toLowerCase();
    }

    public static String convertHexToAscci(String hexValue) {
        StringBuilder output = new StringBuilder("");
        try {
            for (int i = 0; i < hexValue.length(); i += 2) {
                String str = hexValue.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static String byteToString(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(String.format("%02x", byteChar));
        }

        return sb.toString();
    }

    public static String addDay(String date, String format, int add) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            Date mDate = null;
            mDate = dateFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDate);
            cal.add(Calendar.DATE, add);
            Date newDate = cal.getTime();
            return dateFormat.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }




    public static  boolean isActivityFinish(Context context) {
        if(context == null)
            return true ;
        Activity activity = (Activity)context ;
        return activity.isFinishing() || activity.isDestroyed();
    }

    public static long daysBetween(String strBackUpdate) {

        Date backUpDate = null ;
        Date curentDate = null;
        try {
            curentDate = new SimpleDateFormat("yyyy-MM-dd").parse(Utils.getDate());
            backUpDate = new SimpleDateFormat("yyyy-MM-dd").parse(strBackUpdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference =  (curentDate.getTime()-backUpDate.getTime())/86400000;
        return Math.abs(difference);
    }


}
