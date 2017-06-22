package com.prompt.multiplebledeviceconnection.bleUtils;

import java.util.HashMap;

/**
 * Created by root on 9/9/16.
 */

public interface OnDataReceiveInterface {

    void onDataReceived(HashMap<String, String> hashMap);
    void onDataHwReceived(String str);
    void onPrinterResponseReceived(boolean isPrintSuccess);
}
