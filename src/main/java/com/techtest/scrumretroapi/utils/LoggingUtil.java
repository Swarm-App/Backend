package com.techtest.scrumretroapi.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingUtil {
    // Static method to get logger for a class
    public static Log getLogger(Class<?> clazz) {
        return LogFactory.getLog(clazz);
    }
}
