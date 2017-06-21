/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.prompt.multiplebledeviceconnection.bleUtils;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String BLE_TX = "0003cdd2-0000-1000-8000-00805f9b0131";
    public static String BLE_RX = "0003cdd1-0000-1000-8000-00805f9b0131";
//    static {
//        // Sample Services.
//        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "HM 10 Serial");
//        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Device Information Service");
//        // Sample Characteristics.
//        attributes.put(HM_RX_TX,"RX/TX data");
//        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
//    }

        static {
        // Sample Services.
        attributes.put("0003cdd0-0000-1000-8000-00805f9b0131", "BLE DEVICE");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
            attributes.put(BLE_TX,"RX/TX data");
        attributes.put("0003cdd2-0000-1000-8000-00805f9b0131", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
