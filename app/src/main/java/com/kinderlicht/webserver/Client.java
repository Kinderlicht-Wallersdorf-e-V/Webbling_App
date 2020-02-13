package com.kinderlicht.webserver;

import android.util.Log;

import com.kinderlicht.authentication.LoginManager;
import com.kinderlicht.authentication.ServerUser;

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

	public static LoginManager loginManager = new LoginManager();
	private static Socket socket;
	private static final String endingsequence = "<<<endingsequence>>>";
	private static final String key = RSA.getPublicKey()[0] + "/" + RSA.getPublicKey()[1];

	// code,token,key,username,message
	
	public static String establishConnection(int code, String token, String argument) {
		SendMessage toSend = new SendMessage(code, token, key, (ServerUser)loginManager.getUser("Server"), argument);
		return establishConnection(toSend);
	}

	public static String establishConnection(SendMessage toSend) {
		ServerUser user = (ServerUser)loginManager.getUser("Server");
		if(user.isReminded() && !user.hasValidLogin()) {
			return "Invalid login";
		}
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
				System.out.println("[ CLIENT ]    Send message:  " + current);
				if (!user.hasKey()) {
					System.out.println("[ CLIENT ]       Attention:  ServerUser has no key");
					current = new SendMessage(20, "", key, user.getUsername(), "empty");
					bw.write(current + "");
				} else {
					bw.write(current.getEncryptedText(user.getServerKey()));
				}
				bw.flush();

				answer = receive(socket);
				System.out.println("[ CLIENT ] Received answer:  " + answer);
				// decrypt answer
				if (user.hasKey() && !answer.startsWith("30,")) {
					String parts[] = answer.split(",", 2);
					// TODO find first "]" after first "," and delete rest of string
					String key = parts[1].replace("[", "").replace("]", "");
					String aescipher = parts[0];

					String keyarray[] = key.split(",");
					BigInteger[] cypher = new BigInteger[keyarray.length];
					for(int i = 0; i < keyarray.length; i++){
						cypher[i] = new BigInteger(keyarray[i].trim());
					}

					String aeskey = RSA.decrypt(cypher);
					answer = AES.decrypt(aeskey, aescipher);
					System.out.println("[ CLIENT ] Received answer:  " + answer);
				}

				received = new ReceiveMessage(answer);
				System.out.print("[ CLIENT ] Interpretation:  ");
				STATUS_CODES ret = STATUS_CODES.CONNECTION_END;
				for (STATUS_CODES code: STATUS_CODES.values()){
					if  (code.getValue() == received.getCode()) {
						ret = code;
						break;
					}
				}
				
				switch (ret) {
				case LOGIN_FAILED:
					System.out.println("Wrong password or username.");
					// Maybe close connection and ask user for password
					answer = "30,";
					//TODO show login mask for user
					user.setReminded(true);
					break outerwhile;
				case LOGIN_SUCCESS:
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
				case LOGIN_PENDING:
					System.out.println("Not logged in.");
					current = new SendMessage(0, "", key, user.getUsername(), user.getPassword());
					break;
				case MESSAGE_UNKNOWN:
					System.out.println("Message " + toSend + " is not a valid message.");
					break outerwhile;
				case MESSAGE_SUCCESS:
					System.out.println("Message has been processed successfully.");
					break outerwhile;
				case MESSAGE_FAILED:
					System.out.println("Message has correct format but a server-side error occured.");
					break outerwhile;
				case TOKEN_EXPIRED:
					System.out.println("Token expired, login again...");
					current = new SendMessage(0, "", key, user.getUsername(), user.getPassword());
					break;
				case TOKEN_INVALID:
					System.out.println("Token invalid. End connection!");
					break outerwhile;
				case TOKEN_VALID:
					System.out.println("Token is valid!");
					break;
				case CONNECTION_END:
					System.out.println("Server terminated connection. Reason: " + received.getArgument());
					if(received.getArgument().contains("Unexpected failure: ")) {
						//ExceptionView.createAlertDialog("Unerwarteter Fehler", "Der Server hat die Verbindung geschlossen.", "M�glicherweise musst Du Dich erneut anmelden.\nEs gibt ein Problem mit der Serverkommunikation.\nKann das Problem nicht durch eine erneute Anmeldung gel�st werden, wende Dich bitte an matthias.kettl@kinderlicht-wallersdorf.de", new Exception(received.getArgument()));
					}
					break outerwhile;
				case CRYPTO_KEYEXCHANGE:
					System.out.println("Received public key: " + received.getKey());
					user.setServerKey(received.getKey());
					user.setToken(received.getArgument());
					current = toSend;
					if (current.getCode() == 20) {
						// Message exchange is executed
						break outerwhile;
					}
					break;
				default:
					break outerwhile;
				}
				toSend.setToken(user.getToken());
			}
		} catch (java.net.ConnectException exception) {
			System.out.println("\n[ CLIENT ] Connecting to server failed.");
			//ExceptionView.createAlertDialog("Server offline", "Der Server scheint offline zu sein.", "Wartungsarbeiten sind normalerweise innerhalb einer Stunde abgeschlossen.\nGerne kannst Du uns �ber diesen Vorfall informieren.\nE-Mail: service@kinderlicht-wallersdorf.de", exception);
		} catch (IOException e) {
			System.out.println("\n[ CLIENT ] Socket connection can not be established due to an IOException.");
			//ExceptionView.createAlertDialog("Datei-Fehler", "Die .ini Datei konnte nicht gefunden werden.", "Bitte platziere die config.ini in den Programmordner.", e);
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println("\n[ CLIENT ] Closing socket failed.");
				//ExceptionView.createAlertDialog("Server offline", "Der Server scheint offline zu sein. Wartungsarbeiten sind normalerweise innerhalb einer Stunde abgeschlossen.", e);
				//e.printStackTrace();
			}
		}
		return received.getArgument();
	}

	private static String receive(Socket socket) throws IOException {
		byte[] messageByte = new byte[20 * 1024];
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

//	public static void main(String[] args) {
//		getTable(establishConnection(new SendMessage(32, "", key, "mat@kat.de", "none")));
//	}

//	public static void getTable(String sql) {
//		ResultSet rs = new ResultSet(sql);
//		while (rs.hasNext()) {
//			for (int i = 0; i < rs.getLabelCount(); i++) {
//				System.out.print(rs.get(i) + " ");
//			}
//			System.out.println();
//			rs.next();
//		}
//	}

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

	public SendMessage(int code, String token, String publicKey, ServerUser user, String argument) {
		this.code = code;
		if (token.equals(""))
			token = "empty";
		this.token = token;
		this.username = user.getUsername();
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

enum RETURN_CODES {
	LOGIN(0), SQL_SELECT(10), CRYPTO_KEYEXCHANGE(20), WEBLING_ACCOUNT(30), WEBLING_ENTRIES_ACCOUNT(31),
	WEBLING_NOTIFICATION(32);

	private final int value;

	private RETURN_CODES(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

enum STATUS_CODES {
	LOGIN_FAILED(0), LOGIN_SUCCESS(1), LOGIN_PENDING(2), MESSAGE_UNKNOWN(10), MESSAGE_SUCCESS(11), MESSAGE_FAILED(12),
	TOKEN_EXPIRED(20), TOKEN_INVALID(21), TOKEN_VALID(22), CONNECTION_END(30), CRYPTO_KEYEXCHANGE(40);

	private final int value;

	private STATUS_CODES(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}