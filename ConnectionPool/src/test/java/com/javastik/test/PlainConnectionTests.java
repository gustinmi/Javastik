package com.javastik.test;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import com.javastik.database.ConnectionFactory;
import com.javastik.database.DatabaseSingleTransaction;
import com.javastik.database.PlainConnectionFactory;
import com.javastik.database.StatementWithParams;
import com.javastik.database.DatabaseJsonRenderer.JsonRenderType;
import com.javastik.database.StatementWithParams.SqlQueryParam;
import com.javastik.logging.LoggingFactory;
import com.javastik.*;

public class PlainConnectionTests {
    
    static final Logger log = LoggingFactory.loggerForThisClass();
    
    static final Random randomGenerator = new Random();
    
    public static Integer getRandomNumber(int max) {
        return randomGenerator.nextInt(max);
    }
    
    private static DatabaseSingleTransaction database;
    private static ConnectionFactory provider;
    
    @BeforeClass
    public static void before() {
        provider = PlainConnectionFactory.instance;
        database = new DatabaseSingleTransaction(provider); 
    } 

	@Test
	public void plainConnect() throws SQLException {
		Connection connection = provider.getConnection();
		assertFalse("Connection should be opened at this point", connection.isClosed());
	}
	
	@Test
    public void getScalar() throws SQLException {
	    
	    StatementWithParams stmt = new StatementWithParams("select name from test where id = ?"); 
        stmt.addParam(new SqlQueryParam(Types.INTEGER, new Integer(1).toString()));
        
        String scalar = (String) database.getScalar(stmt);
	    assertTrue("Query should return result", scalar != null && !scalar.isEmpty());
	    assertTrue("Value was not read correctly", scalar.equalsIgnoreCase("The very first row"));
        
    }
	
	@Test
    public void getJson() throws SQLException {
        
        StatementWithParams stmt = new StatementWithParams("select * from test where id = ?"); 
        stmt.addParam(new SqlQueryParam(Types.INTEGER, new Integer(1).toString()));
        
        String json = (String) database.getJson(stmt, JsonRenderType.OBJECT);
        assertTrue("Query should return json", json != null && !json.isEmpty());
        
    }

	
	@Test
    public void execUpdateQuery() throws SQLException {
	    
	    StatementWithParams stmt = new StatementWithParams("insert into test (name) values (?)"); 
        stmt.addParam(new SqlQueryParam(Types.VARCHAR, "New row data " + getRandomNumber(10000)));
	    
        String execUpdateQuery = database.execUpdateQuery(stmt);
        assertNotNull("Result of insert cannot be null", execUpdateQuery);
        assertEquals("{ \"ucnt\" : \"1\" }", execUpdateQuery);
	    
	}
	
	@Test
    public void execUpdateQueryReturnInsertId() throws SQLException {
	    
	    StatementWithParams stmt = new StatementWithParams("insert into test (name) values (?)"); 
        stmt.addParam(new SqlQueryParam(Types.VARCHAR, "New row data " + getRandomNumber(10000)));
        
        String execUpdateQuery = database.execUpdateQueryReturnInsertId(stmt);
        assertNotNull("Result of insert cannot be null", execUpdateQuery);
        assertTrue("", execUpdateQuery.indexOf("insertId") >= 0);
	    
	    
	}
	
	
	
	
	
}
