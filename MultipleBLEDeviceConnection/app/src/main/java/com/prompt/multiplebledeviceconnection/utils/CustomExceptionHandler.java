package com.prompt.multiplebledeviceconnection.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class CustomExceptionHandler implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler defaultUEH;
    private String dirName;
    private String url;

    /*
    * if any of the parameters is null, the respective functionality
    * will not be used
    */
    public CustomExceptionHandler(String dirName, String url) {
        this.dirName = dirName;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();

        if (dirName != null) {
            writeToFile(stacktrace);
        }
        defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace) {
        try {
            File myDir = new File(dirName.replace(" ", "") + "_Log");
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            //Store only 10 file in device because of size.
            if (myDir.isDirectory() & myDir.listFiles().length > 20) {
                File[] filelist = myDir.listFiles();
                for (int i = 0; i < filelist.length - 20; i++) {
                    try {
                        filelist[i].delete();
                    } catch (Exception e) {
                    }
                }
            }
            File f = new File(myDir, Utils.getLogFileName());
            FileWriter fr = new FileWriter(f);
            BufferedWriter bos = new BufferedWriter(fr);
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}