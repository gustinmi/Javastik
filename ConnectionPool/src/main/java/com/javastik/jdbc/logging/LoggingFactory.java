package com.javastik.jdbc.logging;

import java.util.logging.Logger;

/** Tale metoda zagotovi, da je logger na clasu vedno definiran z pravilim imenom razreda.
 * @author mitjag
 */
public class LoggingFactory {

    public static final String LOG_LINE_SEPARATOR = System.getProperty("line.separator");
    
    /** Gets the logger for caller class
     * @return
     */
    public static Logger loggerForThisClass() {
        
        // We use the third stack element; second is this method, first is .getStackTrace()
        
        StackTraceElement myCaller = Thread.currentThread().getStackTrace()[2];
        
        return Logger.getLogger(myCaller.getClassName());
    }
    
    
}
