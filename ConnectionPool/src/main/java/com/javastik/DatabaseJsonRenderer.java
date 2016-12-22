package com.javastik;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DatabaseJsonRenderer {

    private static String displayObject(ResultSet rs) throws SQLException {
        StringBuilder pw = new StringBuilder();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        String[] colNames = new String[numColumns + 1];

        for (int i = 1; i < (numColumns + 1); i++)
            colNames[i] = rsmd.getColumnLabel(i);
        
        if (rs.next()) {
            pw.append("{");
            
            for (int colIdx=1; colIdx <= numColumns; colIdx++) {
                Object f = JdbcProtocolNormalizer.getData(rsmd, rs, colIdx, true);
                pw.append("\""+ JdbcProtocolNormalizer.nvl(colNames[colIdx]) + "\"");
                pw.append(":");
                pw.append("\""+ JdbcProtocolNormalizer.nvl(f) + "\"");
                if (colIdx < numColumns)
                    pw.append(",");
            }
            
            pw.append("}");
        }
        
        if (pw.length() < 1) return "{}";
        
        return pw.toString();
        
    }
    
}
