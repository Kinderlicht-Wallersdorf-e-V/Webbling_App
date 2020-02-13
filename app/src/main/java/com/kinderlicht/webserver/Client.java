package com.kinderlicht.webserver;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Client {

	private static Socket socket;
	private static final String endingsequence = "<<<endingsequence>>>";
	private static final String key = RSA.getPublicKey()[0] + "/" + RSA.getPublicKey()[1];

	// code,token,key,username,message

	public static ReceiveMessage establishConnection(User user, SendMessage toSend) {
		ReceiveMessage received = new ReceiveMessage("-1,null,null");
		try {
			String host = "kinderlicht.ddns.net";
			int port = 65432;
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);

			SendMessage current = toSend;

			String answer = "";
			outerwhile: while (!answer.startsWith("30,")) {
				System.out.println("   Send message:  " + current);
				if (!user.hasKey()) {
					System.out.println("      Attention:  User has no key");
					current = new SendMessage(20, "", key, user.getName(), "empty");
					bw.write(current + "");
				} else {
					bw.write(current.getEncryptedText(user.getServerKey()));
				}
				bw.flush();

				answer = receive(socket);
				// decrypt answer
				if (user.hasKey()) {
					String parts[] = answer.split(",", 2);
					String key = parts[1].replace("[", "").replace("]", "");
					String aescipher = parts[0];

					String keyarray[] = key.split(",");
					BigInteger[] cypher = new BigInteger[keyarray.length];
					for(int i = 0; i < keyarray.length; i++){
						cypher[i] = new BigInteger(keyarray[i].trim());
					}

					String aeskey = RSA.decrypt(cypher);
					answer = AES.decrypt(aeskey, aescipher);
					System.out.println("Received answer:  " + answer);
				}

				received = new ReceiveMessage(answer);
				System.out.print(" Interpretation:  ");
				switch (received.getCode()) {
				case -1:
					System.out.println("Invalid message received.");
					break outerwhile;
				case 0:
					System.out.println("Wrong password or username.");
					// Maybe close connection and ask user for password
					break outerwhile;
				case 1:
					System.out.println("Login successful.");
					String[] arguments = received.getArgument().split(",");
					user.setPermission(Integer.parseInt(arguments[0]));
					user.setToken(arguments[1]);
					current = toSend;
					// if message is meant to be a login statement end here
					if (toSend.getCode() == 0) {
						break outerwhile;
					} else {
						break;
					}
				case 2:
					System.out.println("Not logged in.");
					current = new SendMessage(0, "", key, user.getName(), user.getPassword());
					break;
				case 10:
					System.out.println("Message " + toSend + " is not a valid message.");
					break outerwhile;
				case 11:
					System.out.println("Message has been processed successfully.");
					break outerwhile;
				case 12:
					System.out.println("Message has wright format but a server-side error occured.");
					break outerwhile;
				case 20:
					System.out.println("Token expired, login again...");
					current = new SendMessage(0, "", key, user.getName(), user.getPassword());
					break;
				case 21:
					System.out.println("Token invalid, login again...");
					break outerwhile;
				case 22:
					break;
				case 30:
					break outerwhile;
				case 40:
					System.out.println("Received public key: " + received.getKey());
					user.setServerKey(received.getKey());
					user.setToken(received.getArgument());
					current = toSend;
					if(current.getCode() == 20) {
						//Message exchange is executed
						break outerwhile;
					}
					break;
				default:
					break outerwhile;
				}
				toSend.setToken(user.getToken());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return received;
	}

	private static String receive(Socket socket) throws IOException {
		byte[] messageByte = new byte[8 * 1024];
		boolean end = false;
		String dataString = "";

		DataInputStream in = new DataInputStream(socket.getInputStream());

		while (!end) {
			int bytesRead = in.read(messageByte);
			// System.out.println(Base64.getDecoder().decode(messageByte));
			dataString += new String(messageByte, 0, bytesRead);
			if (dataString.endsWith(endingsequence))
				end = true;
		}

		return dataString.replace(endingsequence, "");
	}

	public static void main(String[] args) {
		getTable(establishConnection(new User("mat@kat.de", "iamroot", "", 0),
				new SendMessage(10, "", key, "mat@kat.de", "SELECT * FROM pro_user")).getArgument());
	}

	public static void getTable(String sql) {
		ResultSet rs = new ResultSet(sql);
		while (rs.hasNext()) {
			for (int i = 0; i < rs.getLabelCount(); i++) {
				System.out.print(rs.get(i) + " ");
			}
			System.out.println();
			rs.next();
		}
	}

}

class SendMessage {

	private int code;
	private String token;
	private String argument;
	private String publicKey;
	private String username;

	public SendMessage(int code, String token, String publicKey, String username, String argument) {
		this.code = code;
		if (token.equals(""))
			token = "empty";
		this.token = token;
		this.username = username;
		this.publicKey = publicKey;
		this.argument = argument;
	}

	public SendMessage(int code, String token, String publicKey, User user, String argument) {
		this.code = code;
		if (token.equals(""))
			token = "empty";
		this.token = token;
		this.username = user.getName();
		this.publicKey = publicKey;
		this.argument = argument;
	}

	public String toString() {
		return code + "," + token + "," + publicKey + "," + username + "," + argument;
	}

	public String getEncryptedText(String key) {
		String aeskey = AES.keyGen(16);
		String ret = AES.encrypt(aeskey, toString());
		String parts[] = key.split("/");
		BigInteger[] keys = { new BigInteger(parts[0]), new BigInteger("0"), new BigInteger(parts[1]) };
		BigInteger[] enckey = RSA.encrypt(aeskey, keys);

		String result = "";
		for (BigInteger bigInteger : enckey) {
			result += "/" + bigInteger;
		}
		if(result.length() > 0){
			result = result.substring(1);
		}

		return ret + "," + result;
	}

	public boolean hasToken() {
		return !token.equals("empty");
	}

	public String getUser() {
		return username;
	}

	public int getCode() {
		return code;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}

class ReceiveMessage {

	private int code;
	private String argument;
	private String publicKey;

	public ReceiveMessage(int code, String publicKey, String argument) {
		this.code = code;
		this.publicKey = publicKey;
		this.argument = argument;
	}

	public ReceiveMessage(String message) {
		try {
			String[] parts = message.split(",", 3);
			code = Integer.parseInt(parts[0]);
			argument = parts[2];
			publicKey = parts[1];
		} catch (Exception e) {
			code = -1;
		}
	}

	public String toString() {
		return code + "," + publicKey + "," + argument;
	}

	public int getCode() {
		return code;
	}

	public String getKey() {
		return publicKey;
	}

	public String getArgument() {
		return argument;
	}

}
