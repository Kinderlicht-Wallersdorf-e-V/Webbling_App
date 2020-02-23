package de.kettl.webserver;

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

	private Socket socket;
	private static final String endingsequence = "<<<endingsequence>>>";
	private static final String key = RSA.getPublicKey()[0] + "/" + RSA.getPublicKey()[1];
	private ConnectionSettings settings;
	private ExceptionHandler handler;
	// code,token,key,username,message
	
	public Client(ConnectionSettings settings, ExceptionHandler handler) {
		this.settings = settings;
		this.handler = handler;
	}
	
	public void updateSettings(ConnectionSettings newSettings) {
		settings = newSettings;
	}
	
	public String establishConnection(ReturnCodes code, String argument){
		SendMessage toSend = new SendMessage(code, settings.getUser().getToken(), key, settings.getUser(), argument);
		return establishConnection(toSend);
	}

	private String establishConnection(SendMessage toSend) {
		User user = settings.getUser();
		if(user.isReminded() && !user.hasValidLogin()) {
			return "Invalid login";
		}
		ReceiveMessage received = new ReceiveMessage("-1,null,null");
		try {
			String host = settings.getHost();
			int port = Integer.parseInt(settings.getPort());
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);

			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);

			SendMessage current = toSend;

			String answer = "";
			outerwhile: while (!answer.startsWith(StatusCodes.CONNECTION_END.getValue()+"")) {
				System.out.println("[ CLIENT ]    Send message:  " + current);
				if (!user.hasKey()) {
					System.out.println("[ CLIENT ]       Attention:  ServerUser has no key");
					current = new SendMessage(ReturnCodes.CRYPTO_KEYEXCHANGE, "", key, user.getUsername(), "empty");
					bw.write(current + "");
				} else {
					System.out.println("YOOOOOOOOOOOOOOOOOOOOOOOOO " + user.getServerKey());
					bw.write(current.getEncryptedText(user.getServerKey()));
				}
				bw.flush();

				answer = receive(socket);
				System.out.println("[ CLIENT ] Received answer:  " + answer);
				// decrypt answer
				if (user.hasKey() && !answer.startsWith(StatusCodes.CONNECTION_END.getValue()+"") && !answer.startsWith(StatusCodes.CONNECTION_END_WITH_EXCEPTION.getValue()+"")) {
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
				String interpretation = "[ CLIENT ]  Interpretation:  ";
				StatusCodes ret = StatusCodes.CONNECTION_END;
				for (StatusCodes code: StatusCodes.values()){
					if  (code.getValue() == received.getCode()) {
						ret = code;
						break;
					}
				}
				
				switch (ret) {
				case LOGIN_FAILED:
					System.out.println(interpretation + "Wrong password or username.");
					// Maybe close connection and ask user for password
					answer = StatusCodes.CONNECTION_END.getValue()+",";
					//TODO show login mask for user
					user.setReminded(true);
					break outerwhile;
				case LOGIN_SUCCESS:
					System.out.println(interpretation + "Login successful.");
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
					System.out.println(interpretation + "Not logged in.");
					current = new SendMessage(ReturnCodes.LOGIN, "", key, user.getUsername(), user.getPassword());
					break;
				case MESSAGE_UNKNOWN:
					System.out.println(interpretation + "Message " + toSend + " is not a valid message.");
					break outerwhile;
				case MESSAGE_SUCCESS:
					System.out.println(interpretation + "Message has been processed successfully.");
					answer = StatusCodes.CONNECTION_END.getValue()+",";
					break outerwhile;
				case MESSAGE_FAILED:
					System.out.println(interpretation + "Message has correct format but a server-side error occured.");
					break outerwhile;
				case TOKEN_EXPIRED:
					System.out.println(interpretation + "Token expired, login again...");
					current = new SendMessage(ReturnCodes.LOGIN, "", key, user.getUsername(), user.getPassword());
					break;
				case TOKEN_INVALID:
					System.out.println(interpretation + "Token invalid. End connection!");
					break outerwhile;
				case TOKEN_VALID:
					System.out.println(interpretation + "Token is valid!");
					break;
				case CONNECTION_END_WITH_EXCEPTION:
					handler.handle(ret, new Exception(received.getArgument()), "Verbindung geschlossen", "Die Verbindung wurde wegen eines unerwarteten Fehlers geschlossen.", "Eventuell besteht keine Verbindung zum Server. Ein Neustart des Programms kann m�glicherwei�e helfen.");
					user.setServerKey("");
				case CONNECTION_END:
					System.out.println(interpretation + "Server terminated connection. Reason: " + received.getArgument());
					break outerwhile;
				case CRYPTO_KEYEXCHANGE:
					System.out.println(interpretation + "Received public key: " + received.getKey());
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
			System.out.println("[ CLIENT ] Connecting to server failed.");
			handler.handle(StatusCodes.EXTERNAL_ERROR, exception, "Server offline", "Der Server scheint offline zu sein.", "Wartungsarbeiten sind normalerweise innerhalb einer Stunde abgeschlossen.\nGerne kannst Du uns �ber diesen Vorfall informieren.\nE-Mail: service@kinderlicht-wallersdorf.de");
			//handler.handleException(StatusCodes.EXTERNAL_ERROR, exception, );
		} catch (IOException e) {
			System.out.println("[ CLIENT ] Socket connection can not be established due to an IOException.");
			handler.handle(StatusCodes.EXTERNAL_ERROR, e, "Datei-Fehler oder Server offfline", "M�glicherweise ist die 'config.ini'-Datei nicht richtig platziert. Wenn doch, ist m�glicherweise der Server offline.", "Bitte platziere die config.ini in den Programmordner oder �berpr�fe, ob der Server erreichbar ist.");
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println("[ CLIENT ] Closing socket failed.");
				//throw new ClientException(StatusCodes.EXTERNAL_ERROR, e, "Datei-Fehler", "Die .ini Datei konnte nicht gefunden werden.", "Bitte platziere die config.ini in den Programmordner.");
				//ExceptionView.createAlertDialog("Server offline", "Der Server scheint offline zu sein. Wartungsarbeiten sind normalerweise innerhalb einer Stunde abgeschlossen.", e);
				//e.printStackTrace();
			}
		}
		return received.getArgument();
	}

	private String receive(Socket socket) throws IOException {
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

}   

class SendMessage {

	private int code;
	private String token;
	private String argument;
	private String publicKey;
	private String username;

	public SendMessage(ReturnCodes code, String token, String publicKey, String username, String argument) {
		this.code = code.getValue();
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
		this.username = user.getUsername();
		this.publicKey = publicKey;
		this.argument = argument;
	}
	
	public SendMessage(ReturnCodes code, String token, String publicKey, User user, String argument) {
		this.code = code.getValue();
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
		BigInteger[] keys = { new BigInteger(parts[0]), BigInteger.ZERO, new BigInteger(parts[1]) };
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

	public ReceiveMessage(StatusCodes code, String publicKey, String argument) {
		this.code = code.getValue();
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