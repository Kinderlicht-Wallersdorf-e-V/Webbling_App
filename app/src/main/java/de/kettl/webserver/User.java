package de.kettl.webserver;

public class User {

	private String username;
	private String password;
	
	private boolean validLogin;
	private boolean reminded;
	
	private String token;
	private String serverKey;
	private int permission;
	
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
	
	public User(String username, String password, String token, int permission) {
		this.setUsername(username);
		this.setPassword(password);
		this.setValidLogin(!username.equals("") && !password.equals(""));
		if(!hasValidLogin())
		System.out.println("login");
		this.setReminded(false);
		this.token = token;
		this.permission = permission;
		serverKey = "";
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
