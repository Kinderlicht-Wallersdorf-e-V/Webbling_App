package com.kinderlicht.webserver;

import de.kettl.webserver.ReturnCodes;

public class ServerMessage {

    private ReturnCodes code;
    private String message;

    public ServerMessage(ReturnCodes code, String message){
        this.code = code;
        this.message = message;
    }

    public ReturnCodes getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ServerMessage of(ReturnCodes code, String message){
        return new ServerMessage(code, message);
    }
}
