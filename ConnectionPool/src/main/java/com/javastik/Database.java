package com.javastik;

import java.sql.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.javastik.StatementWithParams.SqlQueryParam;
import com.javastik.logging.LoggingFactory;

public class Database {
    
    static final Logger log = LoggingFactory.loggerForThisClass();
    
    private final ConnectionFactory provider;
    
    public Database(ConnectionFactory provider) {
        super();
        this.provider = provider;
    }

    public Object getScalar(StatementWithParams stmt) {
        
        final long start = System.currentTimeMillis();
        
        final String query = stmt.getQuery();
        final Map<Integer, SqlQueryParam> params = stmt.getParams(); 
        
        if (query == null || query.isEmpty()) throw new IllegalArgumentException("Query cannot be empty");
        
        log.fine("Starting to execute query");
        Object f = null;
        try(final Connection connection = provider.getConnection())  {
            try {
                try (final PreparedStatement statement  = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                    if (params != null) JdbcProtocolNormalizer.setParams(statement, params);
                    boolean isRS = statement.execute();
                    if (isRS){
                        ResultSet rs = statement.getResultSet();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        while (rs.next()) {
                            f = JdbcProtocolNormalizer.getData(rsmd, rs, 1, true);
                        }
                    }
                    log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                    return f;
                }
                
            } catch (SQLException e) {
                log.log(Level.SEVERE, StatementWithParams.toStringCreator(query, params), e);
            } finally {
                log.fine("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (SQLException e1) {
            log.log(Level.SEVERE, "Unable to connect to database. Reason : " + e1.getMessage(), e1);
        }
        
        throw new java.lang.IllegalStateException("Error while executing query");
        
        
    }
   
}
