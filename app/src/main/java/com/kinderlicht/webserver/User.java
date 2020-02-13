package com.kinderlicht.webserver;

public class User {

	private String username;
	private String password;
	private String token;
	private String serverKey;
	private int permission;
	
	public User() {
		
	}
	
	public User(String username, String password, String token, int permission) {
		this.username = username;
		this.password = password;
		this.token = token;
		this.permission = permission;
		serverKey = "";
	}
	
	public String getName() {
		return username;
	}

	public String getPassword() {
		return password;
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
	
	public void sendMessage(int code, String argument, String publicKey) {
		if(argument.equals("pw")) {
			argument = password;
		}
		SendMessage sm = new SendMessage(code, getToken(), publicKey, getName(), argument);
		Client.establishConnection(this, sm);
	}
}
