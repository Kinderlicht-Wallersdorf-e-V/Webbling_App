package com.kinderlicht.webserver;

import de.kettl.webserver.Client;
import de.kettl.webserver.ConnectionSettings;
import de.kettl.webserver.ExceptionHandler;
import de.kettl.webserver.ReturnCodes;
import de.kettl.webserver.StatusCodes;
import de.kettl.webserver.User;

public class ClientWrapper {

    private static Client client = new Client(
            //TODO Add User and password
            new ConnectionSettings(new User("mat@kat.de", "iamroot", "", 0),
                    "kinderlicht.ddns.net", "65432"), new ExceptionHandler() {

        @Override
        public void handle(StatusCodes arg0, Exception arg1, String... arg2) {
            //Interface Exception Handler

            //arg1.printStackTrace();

            switch (arg0) {
                case EXTERNAL_ERROR:
                    //TODO Implementieren was passieren soll (z.B. Dialog/Toast anzeigen)
                    break;
                case CONNECTION_END:
                    //TODO Implementieren was passieren soll
                    //TODO anmeldefenster
                    //TODO config updaten
                    //TODO ConnectionSettings updaten
                    break;
                default:
                    break;
            }
        }
    });

    public static String sendMessage(ReturnCodes code, String argument) {
        try {
            return client.establishConnection(code, argument);
        } catch(Exception e){
            e.printStackTrace();
            return "";
        }

    }

}
