package com.kinderlicht.authentication;

public abstract class User {

	private String username;
	private String password;
	
	private boolean validLogin;
	private boolean reminded;
	
	public User(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
		this.setValidLogin(!username.equals("") && !password.equals(""));
		if(!hasValidLogin())
		System.out.println("login");
		this.setReminded(false);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int hashCode() {
		return getUsername().hashCode();
	}

	public boolean hasValidLogin() {
		return validLogin;
	}

	public void setValidLogin(boolean validLogin) {
		this.validLogin = validLogin;
	}

	public boolean isReminded() {
		return reminded;
	}

	public void setReminded(boolean reminded) {
		this.reminded = reminded;
	}
}
