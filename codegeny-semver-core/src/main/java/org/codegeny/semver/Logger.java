package org.codegeny.semver;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface Logger {
	
	static Logger defaultLogger(Class<?> klass) {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(klass.getName());
		return (f, a) -> logger.info(() -> String.format(f, a));
	}
	
	static Logger none() {
		return (f, a) -> {};
	}
	
	static Logger printerLogger(PrintStream printer) {
		return (f, a) -> printer.printf(f, a).println();
	}
	
	static Logger printerLogger(PrintWriter printer) {
		return (f, a) -> printer.printf(f, a).println();
	}

	void log(String format, Object... args);
}
