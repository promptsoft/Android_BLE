/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 *
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 *
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 *
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 *
 *
 */

package com.prompt.multiplebledeviceconnection.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.prompt.multiplebledeviceconnection.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This is a custom log class that will manage logs in the project. Using the
 * <b>disableLog()</b> all the logs can be disabled in the project during the
 * production stage <b> enableLog()</b> will allow to enable the logs , by
 * default the logs will be visible.<br>
 * *
 */
public class Logger {

    private static String mLogTag = "CySmart Android";
    private static boolean mLogflag = true;
    private static File mDataLoggerDirectory;
    private static File mDataLoggerFile;
    private static File mDataLoggerOldFile;
    private static Context mContext;

    public static void d(String message) {
        show(Log.DEBUG, mLogTag, message);

    }

    public static void d(String tag, String message) {
        show(Log.DEBUG, tag, message);

    }

    public static void w(String message) {
        show(Log.WARN, mLogTag, message);

    }

    public static void i(String message) {
        show(Log.INFO, mLogTag, message);

    }

    public static void e(String message) {
        show(Log.ERROR, mLogTag, message);

    }

    public static void v(String message) {
        show(Log.ERROR, mLogTag, message);

    }

    public static void datalog(String message) {
        // show(Log.INFO, mLogTag, message);
        saveLogData(message);

    }

    /**
     * print log for info/error/debug/warn/verbose
     *
     * @param type : <br>
     *             Log.INFO <br>
     *             Log.ERROR <br>
     *             Log.DEBUG <br>
     *             Log.WARN <br>
     *             Log.VERBOSE Log.
     */
    private static void show(int type, String tag, String msg) {

        if (msg.length() > 4000) {
            Log.i("Length ", msg.length() + "");

            while (msg.length() > 4000) {
                show(type, tag, msg.substring(0, 4000));
                msg = msg.substring(4000, msg.length());

            }
        }
        if (mLogflag)
            switch (type) {
                case Log.INFO:
                    Log.i(tag, msg);
                    break;
                case Log.ERROR:
                    Log.e(tag, msg);
                    break;
                case Log.DEBUG:
                    Log.d(tag, msg);
                    break;
                case Log.WARN:
                    Log.w(tag, msg);
                    break;
                case Log.VERBOSE:
                    Log.v(tag, msg);
                    break;
                case Log.ASSERT:
                    Log.wtf(tag, msg);
                    break;
                default:
                    break;
            }

    }

    /**
     * printStackTrace for exception *
     */
    private static void show(Exception exception) {
        try {
            if (mLogflag)
                exception.printStackTrace();

        } catch (NullPointerException e) {
            Logger.show(e);
        }
    }

    public static boolean enableLog() {
        mLogflag = true;
        return mLogflag;
    }

    public static boolean disableLog() {
        mLogflag = false;
        return mLogflag;
    }

    public static void createDataLoggerFile(Context context) {
        mContext = context;
        try {
            /**
             * Directory
             */
            mDataLoggerDirectory = new File(Environment.getExternalStorageDirectory() +
                    File.separator
                    + context.getResources().getString(R.string.dl_directory));
            if (!mDataLoggerDirectory.exists()) {
                mDataLoggerDirectory.mkdirs();
            }
            /**
             * File  name
             */

            mDataLoggerFile = new File(mDataLoggerDirectory.getAbsoluteFile() + File.separator
                    + Utils.GetDate() + context.getResources().getString(R.string.dl_file_extension));
            if (!mDataLoggerFile.exists()) {
                mDataLoggerFile.createNewFile();
            }
            deleteOLDFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteOLDFiles() {
        /**
         * Delete old file
         */
        File[] allFilesList = mDataLoggerDirectory.listFiles();
        long cutoff = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        for (int pos = 0; pos < allFilesList.length; pos++) {
            File currentFile = allFilesList[pos];
            if (currentFile.lastModified() < cutoff) {
                currentFile.delete();
            }

        }
        mDataLoggerOldFile = new File(mDataLoggerDirectory.getAbsoluteFile() + File.separator
                + Utils.GetDateSevenDaysBack() +
                mContext.getResources().getString(R.string.dl_file_extension));
        if (mDataLoggerOldFile.exists()) {
            mDataLoggerOldFile.delete();
        }

    }

    private static void saveLogData(String message) {
        mDataLoggerFile = new File(mDataLoggerDirectory.getAbsoluteFile() + File.separator
                + Utils.GetDate() + mContext.getResources().getString(R.string.dl_file_extension));
        if (!mDataLoggerFile.exists()) {
            try {
                mDataLoggerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        message = Utils.GetTimeandDate() + message;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(mDataLoggerFile, true),
                    "UTF-8");
            BufferedWriter fbw = new BufferedWriter(writer);
            fbw.write(message);
            fbw.newLine();
            fbw.flush();
            fbw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
