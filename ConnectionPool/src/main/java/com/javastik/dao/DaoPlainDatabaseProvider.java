package com.javastik.dao;

import com.javastik.database.ConnectionFactory;
import com.javastik.database.DatabaseSingleTransaction;
import com.javastik.database.PlainConnectionFactory;

public class DaoPlainDatabaseProvider {

    public static DatabaseSingleTransaction database;
    public static ConnectionFactory provider;
    
    static {
        provider = PlainConnectionFactory.instance;
        database = new DatabaseSingleTransaction(provider); 
    } 

    
    
}
