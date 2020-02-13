package com.kinderlicht.authentication;

import java.util.HashMap;

public class LoginManager {

	private static HashMap<String, User> users;
	
	public LoginManager() {
		users = new HashMap<String, User>();
		users.put("E-Mail", new EmailUser("",""));
		users.put("Server", new ServerUser("mat@ket.de", "iamroot", "", 0));
	}
	
	public User getUser(String key) {
		return users.get(key);
	}
}
