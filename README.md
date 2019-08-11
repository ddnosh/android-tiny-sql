# android-tiny-sql
[![Download](https://api.bintray.com/packages/ddnosh/maven/tinysql/images/download.svg) ](https://bintray.com/ddnosh/maven/tinysql/_latestVersion)  
a tiny sql library for android.

# Solution
1. use BaseDao to handle db operation;
2. use DaoFactory to gen Business Dao file;

# Function
1. object operation to handle db;

# Usage
``` 
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
```