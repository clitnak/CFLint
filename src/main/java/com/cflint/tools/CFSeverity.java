package com.cflint.tools;
/**
* Referenced inside plain.xsl at 158 and cflint-to-findbugs.xsl at 100
*/

public enum CFSeverity {
	INFO("Info"),
	WARNING("Warning"),
	ERROR("Error"),
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