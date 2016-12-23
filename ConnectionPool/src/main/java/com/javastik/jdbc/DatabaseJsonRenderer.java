package com.javastik.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseJsonRenderer {
    
    public static enum JsonRenderType {
        
        /**
         * Primeren za rezultate v tabelarni obliki. Rezultati vsebujejo layout (ime fielda iz baze, ime fielda v rezultatih) in podatke, ki so mapirani glede na layout.  
         */
        ARRAY,
        /**
         * Najbolj naraven tip.  Primeren za rezultate v tabelarni obliki. Vrne array objektov, kjer je IME_DB_STOLPCA : VREDNOST
         */
        ARRAY_ASOCIATIVE,
        /**
         * Enovrstična tabelarna oblika. Primeren za querje, kjer se vrne vrednost več stolpcev za ENO ! vrstico
         */
        OBJECT, 
        /**
         * Isto kot ARRAY, le da je brez layouta
         */
        ARRAY_WITHOUT_LAYOUT
    }
    
    public static String displayResults(Statement stmt, JsonRenderType rType) throws SQLException {
        
        if (stmt == null) throw new IllegalStateException("Statement is empty");
        ResultSet rs = stmt.getResultSet();
        if (rs == null)  throw new IllegalStateException("There is no result set in statement");

        switch (rType) {
        
//            case ARRAY:
//                displayResultSet(pw, rs, stmt.getMaxRows(), false, limitCellLength);
//                break;
//            case ARRAY_ASOCIATIVE:
//                displayResultSetAsoc(pw, rs);
//                break;
//            case ARRAY_WITHOUT_LAYOUT:
//                displayResultSet(pw, rs, stmt.getMaxRows(), true, limitCellLength);
//                break;
            case OBJECT:
                return displayObject(rs);
            default: throw new IllegalArgumentException("Render type not suported :" + rType.name());
                
        }
            
    }
    

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
