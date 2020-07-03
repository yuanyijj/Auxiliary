package com.component.learning.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;


import com.component.learning.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2019/2/20/020
 */


public class LogUtil {
    private static boolean IS_DEBUG = BuildConfig.DEBUG;
    private static String TAG = "LogUtil";

    private static final String LOCAL_PATH = Environment.getExternalStorageDirectory() + "/Android/data/com.hd.uav.inspection/common/";

    //private static final boolean IS_SHOW_LOGFILE = BuildConfig.DEBUG;
    private static final boolean IS_SHOW_LOGFILE = false;
    private static final String ERROR = "error.txt";
    private static final String LOG = "log.txt";//普通日志

    static {
        if (IS_SHOW_LOGFILE) {
            //初始化文件
            File dir = new File(LOCAL_PATH);
            File error = new File(LOCAL_PATH + ERROR);
            File log = new File(LOCAL_PATH + LOG);
            //创建文件夹
            if (!dir.exists()) {
                dir.mkdirs();
            }
            /*//判断文件总大小是否超过MAX_SIZE
            long size = 0;
            if (error.exists()) {
                size += error.length();
            }
            if (log.exists()) {
                size += log.length();
            }*/
            //文件不存在就创建文件
            try {
                if (!error.exists()) {
                    error.createNewFile();
                }
                if (!log.exists()) {
                    log.createNewFile();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 写文件的锁对象
     */
    private static final Object mLogLock = new Object();


    public static void i(String tag, String message) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.i(tag + "-->:" + getTargetStackTraceElement(), message);
        }
        if (IS_SHOW_LOGFILE) {
            writeLog(LOG, tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.e(tag + "-->" + getTargetStackTraceElement(), message);
        }
        if (IS_SHOW_LOGFILE) {
            writeLog(ERROR, tag, message);
        }
    }

    public static void e(String tag, String message, Throwable tr) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.e(tag + "-->" + getTargetStackTraceElement(), message, tr);
        }
        if (IS_SHOW_LOGFILE) {
            writeLog(ERROR, tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.w(tag + "-->:" + getTargetStackTraceElement(), message);
        }
        if (IS_SHOW_LOGFILE) {
            writeLog(LOG, tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.v(tag + "-->:" + getTargetStackTraceElement(), message);
        }
        if (IS_SHOW_LOGFILE) {
            writeLog(LOG, tag, message);
        }
    }

    public static void h(String tag, String message) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.v(tag + "-->:" + getTargetStackTraceElement(), message);
        }
        if (true) {
            writeLog(LOG, tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isEmpty(tag)) {
            tag = TAG;
        }
        if (IS_DEBUG) {
            Log.d(tag + "-->:" + getTargetStackTraceElement(), message);
        }
        if (IS_SHOW_LOGFILE) {
            writeLog(LOG, tag, message);
        }
    }

    private static String getTargetStackTraceElement() {
        StackTraceElement targetStackTrace = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 4) {
            targetStackTrace = stackTrace[4];
        }
        String s = "";
        if (null != targetStackTrace) {
            s = "(" + targetStackTrace.getFileName() + ":"
                    + targetStackTrace.getLineNumber() + ")";
        }
        return s;
    }

    /**
     * 写log
     *
     * @param filePath
     * @param tag
     * @param msg
     */
    private static void writeLog(String filePath, String tag, String msg) {
        if (filePath == null || "".equals(filePath)) {
            return;
        }
        if (tag == null || "".equals(tag)) {
            return;
        }
        if (msg == null || "".equals(msg)) {
            return;
        }
        File file = new File(LOCAL_PATH + filePath);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            String content = formateTime() + "\t" + tag + "\t" + msg + "\n";
            fw.append(content);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建log时间
     *
     * @return time
     */
    @SuppressLint("SimpleDateFormat")
    private static String formateTime() {
        String time = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = simpleDateFormat.format(date);
        return time;
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

}
