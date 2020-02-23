package de.kettl.webserver;

public enum StatusCodes {
	EXTERNAL_ERROR(-1), LOGIN_FAILED(0), LOGIN_SUCCESS(1), LOGIN_PENDING(2), MESSAGE_UNKNOWN(10), MESSAGE_SUCCESS(11),
	MESSAGE_FAILED(12), TOKEN_EXPIRED(20), TOKEN_INVALID(21), TOKEN_VALID(22), CONNECTION_END(30), CONNECTION_END_WITH_EXCEPTION(31),
	CRYPTO_KEYEXCHANGE(40);

	private final int value;

	private StatusCodes(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
