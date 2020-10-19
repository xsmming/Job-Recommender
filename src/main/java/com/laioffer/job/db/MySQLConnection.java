package com.laioffer.job.db;

import com.laioffer.job.entity.Item;

import java.nio.file.FileSystemNotFoundException;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 这个类是一个帮助你建立/关闭db连接的窗口，帮助我们focus在逻辑上
 *
 * */
public class MySQLConnection {
    private Connection conn;  // sql.Connection

    public MySQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(MySQLDBUtil.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setFavoriteItems(String userId, Item item) {
        if (conn == null) {
            System.err.println("DB connnection failed");
            return;
        }
        saveItem(item);
        String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId); // 匹配上面的问号??
            statement.setString(2, item.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsetFavoriteItems(String userId, String itemId) {

        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
            try {
                String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, userId);
                statement.setString(2, itemId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void saveItem(Item item) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        //区别，没有写colomun --> 因为默认是全部colomn
        String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, item.getId());
            statement.setString(2, item.getTitle());
            statement.setString(3, item.getLocation());
            statement.setString(4, item.getCompanyLogo());
            statement.setString(5, item.getUrl());
            statement.executeUpdate();

            sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
            statement = conn.prepareStatement(sql);
            statement.setString(1, item.getId());

            for (String keyword : item.getKeywords()) {
                statement.setString(2, keyword);
                // 这句话不能放loop外面，就会只加一个
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //
    public Set<String> getFavoriteItemIds(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return Collections.emptySet();
        }
        Set<String> favoriteItems = new HashSet<>();
        try {
            String sql = "SELECT item_id FROM history WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);

            // IMPORTANT
            ResultSet rs = statement.executeQuery();    //   这样可以拿到db返回给我的结果
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                favoriteItems.add(itemId);
            }
            // IMPORTANT ABOVE

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    // 先调用getFavoriteItemIds (用user_id 到 history 查对应的一系列item_id)
    // 再用这些item_id 到 items表里查对应的attribute
    // 再调用getKeywords (用item_id 到 keywords 表中查询对应的一系列keyword)
    public Set<Item> getFavoriteItems(String userId) {
        if (conn == null) {
            System.err.println("DB Connection Failed");
            return Collections.emptySet();
        }
        Set<Item> favoriteItems = new HashSet<>();
        Set<String> favoriteItemIds = getFavoriteItemIds(userId);

        String sql = "SELECT * FROM items where item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            for (String itemId : favoriteItemIds) {
                statement.setString(1, itemId);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    Item item = new Item.Builder()
                                                    .id(rs.getString("item_id"))
                                                    .title(rs.getString("name"))
                                                    .url(rs.getString("url"))
                                                    .keywords(getKeywords(itemId))
                                                    .favorite(true)
                                                    .companyLogo(rs.getString("image_url"))
                                                    .location(rs.getString("address"))
                                                    .build();
                    favoriteItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    public Set<String> getKeywords(String itemId) {
        if (conn == null) {
            System.err.println("DB Connection Failed");
            return Collections.emptySet();
        }

        Set<String> keywords = new HashSet<>();
        String sql = "SELECT keyword FROM keywords WHERE item_id = ?";

        // use itemId to search a list of keywords(string) in keywords table
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, itemId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String keyword = rs.getString("keyword");
                keywords.add(keyword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keywords;
    }

    public String getFullname(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return "";
        }
        String name = "";
        String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("full name is " + name);
        return name;
    }

    public boolean verifyLogin(String userId, String password) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return false;
        }
        String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean addUser(String userId, String password, String firstname, String lastname) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return false;
        }

        String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            statement.setString(3, firstname);
            statement.setString(4, lastname);

            return statement.executeUpdate() == 1;  // 返回0说明则已经有相同的了
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
