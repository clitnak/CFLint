package com.cflint.tools;

public enum CFSeverity {
	INFO("Info"),
	CAUTION("Caution"),
	WARNING("Warning"),
	ERROR("Error"),
	CRITICAL("Critical"),
	HIGH("High"),
	FATAL("Fatal");

	private final String value;

	private CFSeverity(String value) {
	    this.value = value;
	}

	public String getName() {
	    return name();
	}

	public String getValue() {
	    return value;
	}

}