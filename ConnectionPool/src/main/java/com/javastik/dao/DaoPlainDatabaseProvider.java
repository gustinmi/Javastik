package com.javastik.dao;

import com.javastik.ConnectionFactory;
import com.javastik.Database;
import com.javastik.PlainConnectionFactory;

public class DaoPlainDatabaseProvider {

    public static Database database;
    public static ConnectionFactory provider;
    
    static {
        provider = PlainConnectionFactory.instance;
        database = new Database(provider); 
    } 

    
    
}
