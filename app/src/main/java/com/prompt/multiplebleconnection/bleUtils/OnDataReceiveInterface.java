package com.prompt.multiplebleconnection.bleUtils;

import java.util.HashMap;

/**
 * Created by root on 9/9/16.
 */

public interface OnDataReceiveInterface {

    void onDataReceived(String deviceAddress,String data);
}
