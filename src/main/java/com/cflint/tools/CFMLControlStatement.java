package com.cflint.tools;

public enum CFMLControlStatement {
	CFIF("cfif"),
	CFSWITCH("cfswitch"),
	CFCASE("cfcase"),
	CFLOOP("cfloop"),
	CFELSE("cfelse"),
	CFELSEIF("cfelseif");
	
	private final String value;

	private CFMLControlStatement(String value) {
	    this.value = value;
	}

	public String getName() {
	    return name();
	}

	public String getValue() {
	    return value;
	}
	
	public static String[] keywordValues() {
		CFMLControlStatement[] keywordsEnum = CFMLControlStatement.values();
		String[] keywords = new String[keywordsEnum.length];
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywordsEnum[i].getValue();
		}
		return keywords;
	}
}
