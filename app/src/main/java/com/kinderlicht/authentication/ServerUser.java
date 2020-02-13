package com.kinderlicht.authentication;

import com.kinderlicht.webserver.Client;

public class ServerUser extends User{

	private String token;
	private String serverKey;
	private int permission;
	
	public ServerUser(String username, String password, String token, int permission) {
		super(username, password);
		this.token = token;
		this.permission = permission;
		serverKey = "";
	}
	
	public int getPermission() {
		return permission;
	}
	
	public void setPermission(int perm) {
		permission = perm;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getServerKey() {
		return serverKey;
	}

	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}
	
	public boolean hasKey() {
		return !serverKey.equals("");
	}
	
	public String sendMessage(int code, String argument) {
		if(argument.equals("pw")) {
			argument = getPassword();
		}
		return Client.establishConnection(code, getToken(), argument);
	}
}
