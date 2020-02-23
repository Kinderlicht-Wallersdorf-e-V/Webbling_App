package de.kettl.webserver;

public interface ExceptionHandler {

	void handle(StatusCodes code, Exception e, String...strings);
}
