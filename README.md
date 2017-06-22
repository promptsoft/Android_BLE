# Multiple BLE Device Connection
This test app demonstrate how to connect and communicate android app with multiple BLE devices using BluetoothLeGatt interface.

# Prerequisites 
1.Android Device with android 4.4.2 and above
2.Bluetooth Low Energy Device which can work in Peripheral Module.

# Installation
1.Import project in Android Studio.
2.Please verify that application have BLUETOOTH and BLUETOOTH_ADMIN permission in menifest file.

If Android Device has Marshmallow or above version then add extra permissions of LOCATION and GPS.

Currently this app supports connection with 2 BLE devices but we can add connection upto 4-7 devices (most device support 4 connection at a time) by implementing BluetoothLeGatt callback method. Each callback method stands for one BLE connection.


Please modify class BluetoothLeSevice to add more BLE devices connection.
