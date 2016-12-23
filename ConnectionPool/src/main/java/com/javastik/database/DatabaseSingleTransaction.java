package com.javastik.database;

import java.sql.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.javastik.database.DatabaseJsonRenderer.JsonRenderType;
import com.javastik.database.StatementWithParams.SqlQueryParam;
import com.javastik.logging.LoggingFactory;

public class DatabaseSingleTransaction {
    
    public static final int MAX_ROWS = 1000;
    
    static final Logger log = LoggingFactory.loggerForThisClass();
    
    private final ConnectionFactory provider;
    
    public DatabaseSingleTransaction(ConnectionFactory provider) {
        super();
        this.provider = provider;
    }
    
    // REAAD METHODS
    
    
    public String getJson(StatementWithParams st, JsonRenderType rType){
        final long start = System.currentTimeMillis();

        log.info("Executing query");
        
        try(final Connection connection = provider.getConnection())  {
            try {
                
                try (final PreparedStatement statement  = connection.prepareStatement(st.getQuery(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                    statement.setMaxRows(MAX_ROWS); /* use paging if 1k is not enough for you*/
                    
                    if (st.getParams() != null) JdbcProtocolNormalizer.setParams(statement, st.getParams());
                    
                    log.finest(StatementWithParams.toStringCreator(st.getQuery(), st.getParams()));  // members logging
                   
                    boolean isRS = statement.execute();
                    if (isRS) {
                        String displayResults = DatabaseJsonRenderer.displayResults(statement, rType);
                        log.finest(displayResults); // json response logging
                        return displayResults;    
                    }
                    
                }
                
            } catch (SQLException e) {
                log.log(Level.SEVERE, StatementWithParams.toStringCreator(st.getQuery(), st.getParams()), e);
            } finally {
                log.fine("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (SQLException e1) {
            log.log(Level.SEVERE, "Unable to connect to database", e1);
        }
            
        throw new java.lang.IllegalStateException("Unexpected error while executing query");
        
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
    
    // INSERT 
    
    public String execUpdateQuery(StatementWithParams st) throws SQLException {
        final Integer executeUpdate = execUpdateQueryInternal(st.getQuery(), st.getParams());
        if (executeUpdate != null && executeUpdate > 0){
            String resp = DatabaseJsonRenderer.displayUpdateCount(executeUpdate);
            return resp;
        }
        return null;
    }
    
    public Integer execUpdateQueryInternal(String query, Map<Integer, SqlQueryParam> params) throws SQLException {
        final long start = System.currentTimeMillis();
        log.finest("Executing query: " + query);
        try(final Connection connection = provider.getConnection())  {
            try{
                try (final PreparedStatement statement = connection.prepareStatement(query)) {
                    if (params != null) JdbcProtocolNormalizer.setParams(statement, params);
                    int executeUpdate = statement.executeUpdate();
                    connection.commit();
                    if (executeUpdate > 0) {
                        log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                        return  executeUpdate;
                    }
                }
            }catch (SQLException e) {
                log.log(Level.SEVERE, StatementWithParams.toStringCreator(query, params), e);
                throw e;
            }finally {
                log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, "Unable to connect to database", e);
            throw e;
        } 
        
        return null;
        
    }
    
    /** 
     * @param st
     * @return
     * @throws SQLException
     */
    public String execUpdateQueryReturnInsertId(StatementWithParams st) throws SQLException {
        return execUpdateQueryReturnInsertId(st.getQuery(), st.getParams(), new String[] { "ID" }, null);
    }
    
    public String execUpdateQueryReturnInsertId(StatementWithParams st, String returnColumns[], String jsonizedObjectTmpl) throws SQLException {
        return execUpdateQueryReturnInsertId(st.getQuery(), st.getParams(), returnColumns, jsonizedObjectTmpl);
    }
    
    /** Executes insert statement and returns generated id of new row. <br>
     * Specify name of column with auto genererator
     * <code>
     * new String[]{"ID"}
     * </code>
     * @param query
     * @param params
     * @param returnColumns
     * @param jsonizedObjectTmpl String template for result : <code>{ myKey : "%s" }</code>
     * @return
     * @throws SQLException
     */
    public String execUpdateQueryReturnInsertId(String query, Map<Integer, SqlQueryParam> params, String returnColumns[], String jsonizedObjectTmpl) throws SQLException {
        final long start = System.currentTimeMillis();
        log.finest("Executing query: " + query);
        try(final Connection connection = provider.getConnection())  {
            try{
                // WARN: oracle ne podpira opcije /*Statement.RETURN_GENERATED_KEYS*/, oz. le ta vrne rowid, in ga moraš naprej queryat.
                // Če pa mu podaš imena stolpca ki ga vrne insert stavek (ID), potem je vse v redu
                //String generatedColumns[] = {"ID"};
                try (final PreparedStatement statement = connection.prepareStatement(query, returnColumns/*Statement.RETURN_GENERATED_KEYS*/)) {
                    if (params != null) JdbcProtocolNormalizer.setParams(statement, params);
                    if(statement.executeUpdate() > 0){
                        
                        log.finest(StatementWithParams.toStringCreator(query, params));  // members logging
                        final ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys!=null && generatedKeys.next()) {
                            final Long id = generatedKeys.getLong(1);
                            log.fine("Insertid " + id);
                            if (jsonizedObjectTmpl == null)
                                return  "{\"insertId\" : " + id.toString() + "}";
                            
                            return String.format(jsonizedObjectTmpl, id.toString());
                        }
                            
                        log.severe("Napaka pri pridobivanju generiranega IDja");
                    }
                }finally{
                    connection.commit();
                }
            }catch (SQLException e) {
                log.log(Level.SEVERE, StatementWithParams.toStringCreator(query, params), e);
                throw e;
            }finally {
                log.finest("Query executed in: " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch(SQLException e){
            log.log(Level.SEVERE, "Unable to connect to database", e);
            throw e;
        } 
        
        throw new IllegalStateException("Error while inserting rows. No row was inserted with query :"+ StatementWithParams.toStringCreator(query, params));
        
    }
    
}
