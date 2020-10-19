package com.laioffer.job.db;

// 这是RDS上的数据库connection，open for connection
public class MySQLDBUtil {
    private static final String INSTANCE = "laiproject-instance.cq3ryhewycyy.us-east-2.rds.amazonaws.com";
    private static final String PORT_NUM = "3306";
    public static final String DB_NAME = "laiproject";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "TF5358979";
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";
}