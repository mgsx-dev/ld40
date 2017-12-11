package net.mgsx.ld40.client;

import com.badlogic.gdx.ApplicationLogger;

public class GwtConsoleApplicationLogger implements ApplicationLogger
{
	@Override
	public void log(String tag, String message, Throwable exception) {
		consoleLog("info " + tag + " " + message + " " + getStackTrace(exception));
	}
	
	@Override
	public void log(String tag, String message) {
		consoleLog("info " + tag + " " + message);
	}
	
	@Override
	public void error(String tag, String message, Throwable exception) {
		consoleError("error " + tag + " " + message + " " + getStackTrace(exception));
	}
	
	@Override
	public void error(String tag, String message) {
		consoleError("error " + tag + " " + message);
	}
	
	@Override
	public void debug(String tag, String message, Throwable exception) {
		consoleLog("debug " + tag + " " + message + " " + getStackTrace(exception));
	}
	
	@Override
	public void debug(String tag, String message) {
		consoleLog("debug " + tag + " " + message);
	}
	
	private String getStackTrace (Throwable e) {
//		StringBuffer buffer = new StringBuffer();
//		for (StackTraceElement trace : e.getStackTrace()) {
//			buffer.append(trace.toString() + "\n");
//		}
//		return buffer.toString();
		
		// XXX stacktrace is just generated javascript code, nothing valuable.
		return e.getMessage() + " " + e.toString();
	}
	
	native static public void consoleLog(String message) /*-{
		console.log(message);
	}-*/;
	native static public void consoleError(String message) /*-{
		console.error(message);
	}-*/;
}
