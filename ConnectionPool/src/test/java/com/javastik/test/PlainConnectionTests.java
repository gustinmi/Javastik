package com.javastik.test;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import org.junit.BeforeClass;
import org.junit.Test;
import com.javastik.*;
import com.javastik.DatabaseJsonRenderer.JsonRenderType;
import com.javastik.StatementWithParams.SqlQueryParam;

public class PlainConnectionTests {
    
    private static Database database;
    private static ConnectionFactory provider;
    
    @BeforeClass
    public static void before() {
        provider = PlainConnectionFactory.instance;
        database = new Database(provider); 
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

}
