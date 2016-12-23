package com.javastik.jdbc.dao;

import com.javastik.jdbc.ConnectionFactory;
import com.javastik.jdbc.Database;
import com.javastik.jdbc.PlainConnectionFactory;

public class DaoPlainDatabaseProvider {

    public static Database database;
    public static ConnectionFactory provider;
    
    static {
        provider = PlainConnectionFactory.instance;
        database = new Database(provider); 
    } 

    
    
}
