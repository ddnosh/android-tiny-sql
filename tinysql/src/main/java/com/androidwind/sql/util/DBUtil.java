package com.androidwind.sql.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class DBUtil {

    private static final String TAG = DBUtil.class.getSimpleName();

    public static String getDatabasePath(String name) {
        //判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if (!sdExist) {//如果不存在,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
            return null;
        } else {//如果存在

            //获取sd卡路径
            String dbDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            //save on external storage
            // String dbDir = getExternalFilesDir(null).getAbsolutePath();
            //save on $external_storage/Android/data/$app_name/files/

            dbDir += "/database";//数据库所在目录
            String dbPath = dbDir + "/" + name;//数据库路径
            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            Log.e(TAG, "database directory: " + dbPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }

            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                try {
                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                isFileCreateSuccess = true;
            }

            //返回数据库文件对象
            if (isFileCreateSuccess) {
                return dbFile.getAbsolutePath();
            } else {
                return null;
            }
        }
    }

}
