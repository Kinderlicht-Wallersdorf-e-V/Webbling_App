package de.kettl.webserver;

public class ConnectionSettings {

	private User user;
	
	private String host;
	private String port;
	
	
	public ConnectionSettings(User user, String host, String port) {
		setUser(user);
		setPort(port);
		setHost(host);
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	

}
