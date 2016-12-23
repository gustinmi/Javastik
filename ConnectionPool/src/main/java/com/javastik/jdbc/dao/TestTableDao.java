package com.javastik.jdbc.dao;

import java.sql.Types;
import com.javastik.jdbc.StatementWithParams;
import com.javastik.jdbc.DatabaseJsonRenderer.JsonRenderType;
import com.javastik.jdbc.StatementWithParams.SqlQueryParam;

public class TestTableDao {
    
    public static final String BY_ID_SQL = "select name from test where id = ?"; 
    
    public static String getRowById(String id) {
        
        StatementWithParams stmt = new StatementWithParams(BY_ID_SQL);  //$NON-NLS-1$
        stmt.addParam(new SqlQueryParam(Types.INTEGER, new Integer(1).toString()));
        
        String scalar = (String) DaoPlainDatabaseProvider.database.getJson(stmt, JsonRenderType.OBJECT);
        return scalar;
        
    }
    
    
}
