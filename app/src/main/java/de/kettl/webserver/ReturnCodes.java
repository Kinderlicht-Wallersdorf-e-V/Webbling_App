package de.kettl.webserver;

public enum ReturnCodes {
	LOGIN(0), SQL_SELECT(10), CRYPTO_KEYEXCHANGE(20), WEBLING_ACCOUNT(30), WEBLING_ENTRIES_ACCOUNT(31),
	WEBLING_NOTIFICATION(32), WEBLING_POST_MEMBER(33);

	private final int value;

	private ReturnCodes(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
