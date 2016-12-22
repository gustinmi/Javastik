package com.javastik;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import com.javastik.StatementWithParams.SqlQueryParam;


/**
 * @author mitjag
 *
 */
public class JdbcProtocolNormalizer {
	
	public static final Logger log = com.javastik.logging.LoggingFactory.loggerForThisClass();
	 
    public static class DateTimeNormalizer {
   
        private static final String JSON_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat(JSON_TIMESTAMP_FORMAT);
        
        private static final String JSON_DATEONLY_FORMAT = "yyyy-MM-dd";
        private static final SimpleDateFormat jsonDateOnlyFormat = new SimpleDateFormat(JSON_DATEONLY_FORMAT);
        
        public static String formatJsonTimestamp(final Timestamp timestamp) {
            if (timestamp == null) return "";
            synchronized (jsonDateFormat) {
                return jsonDateFormat.format(timestamp);
            }
        }
        
        public static String formatJsonDateTime(final Date dt) {
            if (dt==null) return "";
            synchronized (jsonDateFormat) {
                return jsonDateFormat.format(dt);
            }
        }

        
        public static String formatJsonDateonly(final Date dt) {
            if (dt==null) return "";
            synchronized (jsonDateOnlyFormat) {
                return jsonDateOnlyFormat.format(dt);
            }
        }
        
    }
    
    public static class StringNormalizer {
    
        public static String jsonizeString(String value){
            if(value == null) return "";
            
            CharSequence s1 = "\"";
            CharSequence s2 = "\\u0022";
            
            return value.replace(s1, s2).replace("\r\n", "").replace("\n", "");    //ali lahko povsod zamenjamo preskoke v novo vrstico?
        }
        
    }
    
    public static String nvl(Object s) {
        return (s != null) ? String.valueOf(s) : "";
    }
  
    public static void setParams(PreparedStatement stmt, Map<Integer, SqlQueryParam> params) throws SQLException {
        for (Map.Entry<Integer, SqlQueryParam> entry : params.entrySet()) {
            int ndx = entry.getKey();
            SqlQueryParam param = entry.getValue();
            int type = param.getDbType();
            
            Object value = strToDbValue(type, param.getDbVal());
            //if(value != null)
                stmt.setObject(ndx, value);
            //else
            //    stmt.setNull(ndx, type);
        }
    }
	
    /** Retrieves data from ResultSet. Correct type is coersed.
     * @param rsm
     * @param rs
     * @param colIdx
     * @param dateTimeOnly TODO
     * @return
     * @throws SQLException
     */
    public static Object getData(ResultSetMetaData rsm, ResultSet rs, int colIdx, boolean dateTimeOnly) throws SQLException{
		Object dbVal = null;
        switch (rsm.getColumnType(colIdx)) {
            case Types.BIGINT: {
                dbVal = rs.getLong(colIdx);
                break;
            }
            case Types.INTEGER: {
                dbVal = rs.getInt(colIdx);
                break;
            }
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
            {
                if(dateTimeOnly) {
                    Date tmp = rs.getDate(colIdx);
                    if (tmp!=null) {
                        if(dateTimeOnly) dbVal = DateTimeNormalizer.formatJsonDateonly(tmp);
                        break;
                    } 
                    
                }else {
                    Timestamp timestamp = rs.getTimestamp(colIdx);
                    if (timestamp != null){
                        dbVal = DateTimeNormalizer.formatJsonTimestamp(timestamp);
                    }
                        
                }
              
                break;
            }
            case Types.FLOAT: {
                dbVal = rs.getFloat(colIdx);
                break;
            }
            case Types.DOUBLE: {
                dbVal = rs.getDouble(colIdx);
                break;
            }
            case Types.BOOLEAN: {
                dbVal = rs.getBoolean(colIdx);
                break;
            }
            default: {
            	dbVal = StringNormalizer.jsonizeString(rs.getString(colIdx));
            }
        }
        
        return dbVal;
		
	}
    
	/** Gets styped value from string
	 * @param type you want to output {@link java.sql.Types} 
	 * @param s plain string value
	 * @return object that you cast later
	 * @throws SQLException
	 */
	public static Object strToDbValue(int type, String s) throws SQLException {
        if (s == null || s.isEmpty()) {
            return null;
        } else if (type == Types.BIGINT) {
            return new BigInteger(s);
        } else if (type == Types.BIT) {
            return s.getBytes()[0];
        } else if (type == Types.BOOLEAN || type == Types.BINARY) {
            return Boolean.valueOf(s);
        } else if (type == Types.CHAR) {
            return s.charAt(0);
        } else if (type == Types.DATE) {
            try {
                return new java.sql.Date(new SimpleDateFormat("dd.MM.yyyy").parse(s).getTime());
            } catch (ParseException e) {
                throw new SQLException("Error parsing the date (dd.MM.yyyy): " + s);
            }
        } else if (type == Types.DECIMAL || type == Types.NUMERIC) {
            return new BigDecimal(s);
        } else if (type == Types.DOUBLE) {
            return Double.parseDouble(s);
        } else if (type == Types.FLOAT || type == Types.REAL) {
            return Float.parseFloat(s);
        } else if (type == Types.INTEGER) {
            return Integer.parseInt(s);
        } else if (type == Types.LONGVARBINARY || type == Types.VARBINARY) {
            return s.getBytes();
        } else if (type == Types.LONGVARCHAR || type == Types.VARCHAR) {
            return s;
        } else if (type == Types.SMALLINT) {
            return Short.parseShort(s);
        } else if (type == Types.TIME) {
            try {
                return new Time(new SimpleDateFormat("yyyy-mm-dd hh:MM:ss").parse(s).getTime());
            } catch (ParseException e) {
                throw new SQLException("Error parsing the date (yyyy-mm-dd hh:MM:ss): " + s);
            }
        } else if (type == Types.TIMESTAMP) {
            try {
                return new Timestamp(new SimpleDateFormat("yyyy-mm-dd hh:MM:ss").parse(s).getTime());
            } catch (ParseException e) {
                throw new SQLException("Error parsing the date (yyyy-mm-dd hh:MM:ss): " + s);
            }
        } else if (type == Types.TINYINT) {
            return Byte.parseByte(s);
        } else {
            return null;
        }
    }
	
}
