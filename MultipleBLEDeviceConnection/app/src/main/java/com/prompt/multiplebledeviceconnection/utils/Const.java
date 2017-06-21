package com.prompt.multiplebledeviceconnection.utils;

import android.Manifest;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Const {
    public static final String URL_BASE = "http://api.androidhive.info/volley/";
    public static final String URL_IMAGE = "http://api.androidhive.info/volley/volley-image.jpg";
    public static final String METHOD_JSON_OBJECT = "person_object.json";
    public static final String METHOD_JSON_ARRAY = "person_array.json";
    public static final String METHOD_STRING = "string_response.html";


    public static final String DATE_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_DD_MM_YYYY = "dd-MM-yyyy";


    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    public static final BigDecimal INCRE_01 = new BigDecimal("0.1");
    public static final BigDecimal INCRE_1 = new BigDecimal("1");

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final String DATE_TIME = "yyyy-MM-dd hh:mm:ss";
    public static final int STATUS_MT_NOT_FOUND = 1001;
    public static final int STATUS_DIFF_CALCULATION_TYPE = 1002;
    public static final int STATUS_SAME_RATE = 1003;
    public static final int STATUS_SUCCESS = 1111;
    public static final int STATUS_RECORD_NOT_FOUND = 1004;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static int width;
    public static int height;
    public static final int DIALOG_DISPLAY_TIME = 1500;


    //Preferences name
    public static final String PREF_FILE = "amcs_prefs";
    public static final String PREF_LOGIN = "login_status";
    public static final String PREF_INSTALLATION_DETAIL = "installation_detail";
    public static final String COL_IS_LOGIN = "is_login";
    public static final String COL_IS_INSTALED = "is_instaled";
    public static final String PREF_SOCIETY_CODE = "societyCode";
    public static final String PREF_USER_ID = "userid";
    public static final String PREF_DPU_TYPE = "dpuType";
    public static final String PREF_DPU_MILKTYPE = "dpuMilkType";
    public static final String PREF_USER_NAME = "username";
    public static final String PREF_PRINTER_TYPE = "printer_type";
    public static final String PREF_USER_ROLE = "userrole";
    //    public static final String PRINT_ENGLISH = "\u001B\u0040\u001C\u0043\u0000";   //Nirmal
//    public static final String PRINT_ENGLISH = "\u001B@\u001CC\u0000\u001B\u0057\u0002\u001B\u0031\u0003";   //Nirmal
    public static final String PREF_SSID = "ssid";
    public static final String PREF_SSID_PASSWORD = "ssidPassword";


    public static final String PREF_SOCIETY_ID = "SocietyId";
    public static final String PREF_FINANCIAL_YEAR = "financialYear";
    public static final String PREF_CONNECTION_TYPE = "connectionType";
    //User Roles
    public static final int ADMIN = 0;
    public static final int SECRETARY = 1;
    public static final int OPERATOR = 2;
    public static final int TESTER = 3;
    public static final int USER = 4;
    public static final int SUPERVISOR = 5;

    public static int USER_ROLE = 6;

    public static int USER_ID = 3;
    public static final String ACCESS_DENIED = "Access Denied";
    public static String NoOfString = "NoOfString";

    public static String HardwareId = "HardwareId";
    public static String UpdateSuccessMesssage = "Data Updated Sucessfully";

    public static String InsertSuccessMesssage = "Data Updated Sucessfully";
    public static final String strFAT = "FAT", strCLR = "CLR", strFKG = "FKG", strSNFFORMULA1 = "SNFFORMULA1", strSNFFORMULA2 = "SNFFORMULA2",
    strSNF = "SNF", strQTY = "QTY", strSKG = "SKG", strSKC = "SKC", strDKC = "DKC";
    public static final int ID_ALERT = 8080;
    public static String financialYear = "1718";

    //public static String financialYear = "1516";
    public static String NO_HARDWARE = "No Hardware";
    public static final String START = "^";
    public static final String END = "$";
    public static final String QUOTE = "\"";
    public static final String COLON = ":";
    public static final String SEPARATOR = ",";

    public static final int PORT = 8180;
    public static final int ACK_NONE = 0;
    public static final int ACK_SUCCESS = 1;

    public static final int ACK_FAILURE = 2;
    public static final String F_1 = "F1";
    public static final String F_2 = "F2";
    public static final String F_3 = "F3";
    public static final String F_4 = "F4";
    public static final String F_5 = "F5";
    public static final String F_6 = "F6";
    public static final String F_7 = "F7";


    public static final String F_8 = "F8";
    public static final String COMPORT_DATA_REQ = "127";
    public static final String COMPORT_DISP_REQ = "8";


    public static final String COMPORT_PRINT_REQ = "4";
    public static final int COMM_AUTO = 0;
    public static final int COMM_MANN = 1;

    public static final int COMM_REPO = 2;
    public static final int COMPORT_NONE = 100;
    public static final int COMPORT_SUCC = 101;


    public static final int COMPORT_FAIL = 102;
    public static final int HARDWARE_NONE = 200;
    public static final int HARDWARE_SUCC = 201;


    public static final int HARDWARE_FAIL = 202;
    public static final int SYS_SETTING_NONE = 300;
    public static final int SYS_SETTING_SUCC = 301;

    public static final int SYS_SETTING_FAIL = 302;
    public static final int HARDWARE_DETAIL_NONE = 400;
    public static final int HARDWARE_DETAIL_SUCC = 401;


    public static final int HARDWARE_DETAIL_FAIL = 402;
    public static final int WIRELESS_SETT_NONE = 500;
    public static final int WIRELESS_SETT_SUCC = 501;

    public static final int WIRELESS_SETT_FAIL = 502;
    public static final int TARE_RESPONCE_NONE = 600;
    public static final int TARE_RESPONCE_SUCC = 601;


    public static final int TARE_RESPONCE_FAIL = 602;
    public static final int KEY_GUJARATI = 2;
    public static final int KEY_ENGLISH = 1;



    public static final String PRINTER_SETT_BIG_ENGLISH = "\u001B@\u001CC\u0000\u001BW\u0003\u001B1\u0007";//TODO original
    public static final String PRINTER_SETT_SMALL_ENGLISH = "\u001B@\u001CC\u0000\u001BW\u0002\u001B1\u0007";

    public static final String PRINTER_SETT_REPORT_ENGLISH = "\u001B@\u001CC\u0000\u001BV\u0003\u001BU\u0002\u001B1\u0007";
    public static final String PRINTER_SETT_BIG_HINDI = "\u001B@\u001BC\u0001\u001BW\u0003\u001B1\u0007";//TODO original
    public static final String PRINTER_SETT_SMALL_HINDI = "\u001B@\u001BC\u0001\u001BW\u0002\u001B1\u0007";


    public static final String PRINTER_SETT_REPORT_HINDI = "\u001B@\u001BC\u0001\u001BV\u0003\u001BU\u0002\u001B1\u0007";
    public static final String PRINTER_SETT_BIG_GUJARATI = "\u001B@\u001BC\u0002\u001BW\u0003\u001B1\u0007";//TODO original
    public static final String PRINTER_SETT_SMALL_GUJARATI = "\u001B@\u001BC\u0002\u001BW\u0002\u001B1\u0007";

    public static final String PRINTER_SETT_REPORT_GUJARATI = "\u001B@\u001BC\u0002\u001BV\u0003\u001BU\u0002\u001B1\u0007";
    //TODO MilkCollection Slip setting for Skipover performance
    public static final String PRINTER_SKIP_OVER_SETT = "\u001B\u0043\u0001\u001B\u004E";

    public static final String PRINTER_FORM_FEED = "\f";

    //TODO For English
    public static final byte[] PRINT_ENGLISH = {0x1B, 0x40, 0x1C, 0x43, 0x00, 0x1B, 0x57, 0x03, 0x1B, 0x31, 0x07};//TODO actual
    public static final byte[] PRINT_ENGLISH_SMALL_B = {0x1B, 0x40, 0x1C, 0x43, 0x00, 0x1B, 0x57, 0x01, 0x1B, 0x31, 0x07};//TODO actual
//    public static final byte[] PRINT_ENGLISH = {0x1B, 0x40, 0x1C, 0x43, 0x00};//TODO small font

    //TODO for Hindi
             public static final byte[] PRINT_HINDI = {0x1C, 0x43, 0x01, 0x1B, 0x55, 0x01, 0x1B, 0x56, 0x02,0x1B, 0x31, 0x01}; //TODO ajay
       public static final byte[] PRINT_HINDI_SMALL = {0x1C, 0x43, 0x01, 0x1B, 0x55, 0x01, 0x1B, 0x56, 0x01 ,0x1B, 0x31, 0x01}; //TODO ajay
    //TODO For Gujarati
    public static final byte[] PRINT_GUJARATI = {0x1B, 0x40, 0x1C, 0x43, 0x02, 0x1B, 0x55, 0x01, 0x1B, 0x56, 0x02, 0x1B, 0x31, 0x01}; //TODO actual
//    public static final byte[] PRINT_GUJARATI = {0x1B, 0x40, 0x1C, 0x43, 0x02};//TODO small font
    public static final byte[] PRINT_GUJARATI_SMALL = {0x1B, 0x40, 0x1C, 0x43, 0x02, 0x1B, 0x55, 0x01, 0x1B, 0x56, 0x01, 0x1B, 0x31, 0x01};

//    public static final String PRINT_GUJARATI = "\u001B\u0040\u001C\u0043\u0002\u001B\u0055\u0001\u001B\u0056\u0002\u001B\u0031\u0001";

/*
    TODO Printer Setting in HEX Format
    $1C$43$00 -- english language
    $1C$43$01 -- Hindi
    $1C$43$02 -- Gujarati

    SET BIG:
    1B 40
    1B 57 03
    1B 31 07

    SET SMALL:
    1B 40
    1B 57 02
    1B 31 07

    SET REPORT:
    1B 40
    1B 56 03
    1B 55 02
    1B 31 07

    */
    public static String strType2[] = new String[5];


    public static String strType9[] = new String[5];
    public static final String MATRIX_KEYBOARD = "0";
    public static final String SSID = "AMCS_Tablet_Test2";

    public static final String PASS = "1234abcd";

    public static final String PREF_OUT_OF_RANGE = "out_of_range_val";

    //Prefs for BLE Address
    public static final String BLE_MAC1 = "BLE1";
    public static final String BLE_MAC2 = "BLE2";
    public static final String BLE_MAC3 = "BLE3";
    public static final String BLE_MAC4 = "BLE4";
    public static final String HW_BLE_MAC = "BLE_HW";


    public static final String EXTRA_BYTE_VALUE = "EXTRA_BYTE_VALUE";

    public static final String EXTRA_BLE_DEVICE_ADDRESS = "BLE DEVICE ADDRESS";
    public static final String EXTRA_BLE_DEVICE_NAME = "BLE DEVICE NAME";


    //TODO Constant for settings
    public static final int PRINTER_SELECT = 0;
    public static final int PRINTER_SERIAL = 1;
    public static final int PRINTER_USB = 2;

    public static final int DEFAULT_MILK_TYPE_SELECT = 0;
    public static final int DEFAULT_MILK_TYPE_COW = 1;
    public static final int DEFAULT_MILK_TYPE_BUFF = 2;
    public static final int DEFAULT_MILK_TYPE_MIX = 3;

    public static final int MENU_VIEW_GRID = 0;
    public static final int MENU_VIEW_LIST = 1;

    public static final String COLLECTION_TYPE_LITER = "L";
    public static final String COLLECTION_TYPE_KG = "K";

    public static final int MILK_RATE_TYPE_SELECT = 0;
    public static final int MILK_RATE_TYPE_FAT = 1;
    public static final int MILK_RATE_TYPE_FATLR = 2;
    public static final int MILK_RATE_TYPE_FATSNF = 3;
    public static final int MILK_RATE_TYPE_SOLID = 4;

    public static final int NO = 0;
    public static final int YES = 1;


    public static String STATIC_DEVICE_ID = "868981026653701";

    public static final String FONTSIZE_20DP = "20";
    public static final String FONTSIZE_23DP = "23";
    public static final String FONTSIZE_26DP = "26";
    public static final String PREF_FONTSIZE = FONTSIZE_20DP;

    public static final String PREF_AUTOBACKUP = "autoBackup";
    public static final String PREF_LAST_BACKUP_DATE = "lastBackupDate";


    public static final float DEFAULT_TEXT_SIZE= Float.parseFloat(FONTSIZE_20DP);

    public static String CONNECTION_MODE = "" ;
    public  static final String WIFI = "Wifi" ;
    public  static  final String BLUETOOTH= "Bluetooth" ;
    public  static  final String BLUETOOTH_HW = "BluetoothHW" ;
    public static int USB_PRINTER_LINE_PER_PAGE = 65;
}
