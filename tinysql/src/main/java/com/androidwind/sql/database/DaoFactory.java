package com.androidwind.sql.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class DaoFactory {

    private String databasePath;
    private SQLiteDatabase sqLiteDatabase;

    private static volatile DaoFactory sInstance = null;

    private DaoFactory() {
        databasePath = "data/data/com.androidwind.sql.sample/test.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(databasePath, null);
    }

    public static DaoFactory getInstance() {
        if (sInstance == null) {
            synchronized (DaoFactory.class) {
                if (sInstance == null) {
                    sInstance = new DaoFactory();
                }
            }
        }
        return sInstance;
    }

    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass) {
        BaseDao baseDao = null;
        if (map.get(daoClass.getSimpleName()) != null) {
            return (T) map.get(daoClass.getSimpleName());
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase);
            map.put(daoClass.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;

    }
}
