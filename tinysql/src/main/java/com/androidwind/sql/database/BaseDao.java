package com.androidwind.sql.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.androidwind.sql.annotation.DataBaseField;
import com.androidwind.sql.annotation.DataBaseTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */

public class BaseDao<T> implements IDao<T> {

    private SQLiteDatabase sqLiteDatabase;

    private String tableName;

    private boolean isInit = false;

    private HashMap<String, Field> cacheMap;

    public boolean init(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        if (!isInit) {
            if (getTClass().getAnnotation(DataBaseTable.class) == null) {
                tableName = getTClass().getSimpleName();
            } else {
                tableName = getTClass().getAnnotation(DataBaseTable.class).value();
            }

            if (!sqLiteDatabase.isOpen()) {
                return false;
            }

            String createTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(createTableSql);
            cacheMap = new HashMap<>();
            initCacheMap();
            isInit = true;
        }
        return isInit;
    }

    public Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private void initCacheMap() {
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        Field[] columnFields = getTClass().getDeclaredFields();
        for (Field field : columnFields) {
            field.setAccessible(true);
        }

        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : columnFields) {
                String fieldName = null;
                if (field.getAnnotation(DataBaseField.class) != null) {
                    fieldName = field.getAnnotation(DataBaseField.class).value();
                } else {
                    fieldName = field.getName();
                }

                if (columnName.equals(fieldName)) {
                    columnField = field;
                    break;
                }
            }
            if (columnField != null) {
                cacheMap.put(columnName, columnField);
            }
        }
    }

    private String getCreateTableSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName + "(");
        Field[] fields = getTClass().getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (field.getAnnotation(DataBaseField.class) != null) {
                if (type == String.class) {
                    stringBuffer.append(field.getAnnotation(DataBaseField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DataBaseField.class).value() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DataBaseField.class).value() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DataBaseField.class).value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DataBaseField.class).value() + " BLOB,");
                } else {
                    continue;
                }
            } else {
                if (type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOB,");
                } else {
                    continue;
                }
            }

        }
        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    @Override
    public long insert(T entity) {

        Map<String, Object> map = getValues(entity);
        ContentValues values = getContentValues(map);
        long result = sqLiteDatabase.insert(tableName, null, values);
        return result;
    }

    @Override
    public long update(T entity, T where) {
        int result = -1;
        Map values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        Map whereCause = getValues(where);
        Condition condition = new Condition(whereCause);
        result = sqLiteDatabase.update(tableName, contentValues, condition.whereCasue, condition.whereArgs);
        return result;
    }

    @Override
    public int delete(T where) {
        Map map = getValues(where);
        Condition condition = new Condition(map);
        int result = sqLiteDatabase.delete(tableName, condition.whereCasue, condition.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map map = getValues(where);
        String limitString = null;
        if(startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = sqLiteDatabase.query(tableName,null,condition.whereCasue,condition.whereArgs,null,
                null,orderBy,limitString);
        List<T> result = getResult(cursor,where);
        return result;
    }

    private class Condition {
        private String whereCasue;
        private String[] whereArgs;

        public Condition(Map<String, String> whereCasue) {
            ArrayList list = new ArrayList();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");
            Set keys = whereCasue.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = whereCasue.get(key);
                if (value != null) {
                    stringBuilder.append(" and " + key + "=?");
                    list.add(value);
                }
            }
            this.whereCasue = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }

    private List<T> getResult(Cursor cursor, T obj) {
        ArrayList list = new ArrayList();
        Object item = null;
        while (cursor.moveToNext()) {
            try {
                item = obj.getClass().newInstance();

                Iterator iterator = cacheMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String columnName = (String) entry.getKey();
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    Field field = (Field) entry.getValue();
                    Class type = field.getType();

                    if (columnIndex != -1) {
                        if (type == String.class) {
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            field.set(item, cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    private ContentValues getContentValues(Map<String, Object> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = map.get(key);
            Class<?> type = value.getClass();
            if (type == String.class) {
                contentValues.put(key, (String) value);
            } else if (type == Double.class) {
                contentValues.put(key, (Double) value);
            } else if (type == Integer.class) {
                contentValues.put(key, (Integer) value);
            } else if (type == Long.class) {
                contentValues.put(key, (Long) value);
            } else if (type == byte[].class) {
                contentValues.put(key, (Byte) value);
            } else {
                continue;
            }
        }
        return contentValues;
    }

    private Map<String, Object> getValues(T entity) {
        HashMap<String, Object> map = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                String key = null;
                if (field.getAnnotation(DataBaseField.class) != null) {
                    key = field.getAnnotation(DataBaseField.class).value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}

