package com.javastik.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.javastik.jdbc.logging.LoggingFactory;

public class PlainConnectionFactory implements ConnectionFactory {
    
    static final Logger log = LoggingFactory.loggerForThisClass();
    
    public static final PlainConnectionFactory instance = new PlainConnectionFactory();
    
    private PlainConnectionFactory() {
        try {

            Class.forName("org.h2.Driver"); // load class and trigger sdtatic initialization

            log.info("JDBC driver class loaded");
            
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("Could not find the driver jar", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        try {
            
            log.info("Getting new connection");
            
            // get the database via java JNDI syntax  
            return DriverManager.getConnection("jdbc:h2:mem:myDataBase;INIT=RUNSCRIPT FROM 'classpath:h2/init.sql'", "sa", "");
            
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }
    	
	
}
