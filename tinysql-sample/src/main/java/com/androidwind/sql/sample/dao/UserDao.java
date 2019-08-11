package com.androidwind.sql.sample.dao;

import android.util.Log;

import com.androidwind.sql.database.BaseDao;
import com.androidwind.sql.sample.bean.User;

import java.util.List;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class UserDao extends BaseDao<User> {

    //业务相关的数据库处理
    @Override
    public long insert(User entity) {
        List<User> list = query(new User());
        User where = null;
        for (User user : list) {
            where = new User();
            where.setId(user.getId());
            update(entity, where);
        }
        return super.insert(entity);
    }
}
