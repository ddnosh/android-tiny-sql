package com.androidwind.sql.database;

import java.util.List;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public interface IDao<T> {
    long insert(T entity);

    long update(T entity, T where);

    int delete(T where);

    List<T> query(T where);
}
