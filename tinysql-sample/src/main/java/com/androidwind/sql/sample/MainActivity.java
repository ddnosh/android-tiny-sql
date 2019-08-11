package com.androidwind.sql.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidwind.sql.database.DaoFactory;
import com.androidwind.sql.sample.bean.User;
import com.androidwind.sql.sample.dao.UserDao;

import java.util.List;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TinySQL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void insert(View view) {
        UserDao userDao = DaoFactory.getInstance().getBaseDao(UserDao.class);
        userDao.insert(new User("1", "Jack", "123456"));
        Toast.makeText(this, "插入成功", Toast.LENGTH_SHORT).show();
    }

    public void update(View view) {
        UserDao userDao = DaoFactory.getInstance().getBaseDao(UserDao.class);
        User user = new User();
        user.setName("Tom");
        User where = new User();
        where.setId("1");
        userDao.update(user, where);
        Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
    }

    public void delete(View view) {
        UserDao userDao = DaoFactory.getInstance().getBaseDao(UserDao.class);
        User where = new User();
        where.setName("Tom");
        userDao.delete(where);
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
    }

    public void query(View view) {
        UserDao userDao = DaoFactory.getInstance().getBaseDao(UserDao.class);
        User where = new User();
        where.setPassword("123456");
        List<User> list = userDao.query(where);
        Log.d(TAG, "list.size =========" + list.size());
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, list.get(i) + " =====i=====" + i);
        }
        Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show();
    }
}
