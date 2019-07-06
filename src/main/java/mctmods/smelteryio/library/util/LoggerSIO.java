package mctmods.smelteryio.library.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LoggerSIO {

	public static Logger logger;

	public static void log(Level logLevel, Object object) {
		logger.log(logLevel, String.valueOf(object));
	}

	public static void error(Object object)	{
		log(Level.ERROR, object);
	}

	public static void info(Object object) {
		log(Level.INFO, object);
	}

	public static void warn(Object object) {
		log(Level.WARN, object);
	}

}
