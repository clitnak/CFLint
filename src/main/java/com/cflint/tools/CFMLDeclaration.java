package com.cflint.tools;

public enum CFMLDeclaration {
	CFFUNCTION("cfif"),
	CFCOMPONENT("cfcomponent");
	
	private final String value;

	private CFMLDeclaration(String value) {
	    this.value = value;
	}

	public String getName() {
	    return name();
	}

	public String getValue() {
	    return value;
	}
	
	public static String[] keywordValues() {
		CFMLDeclaration[] keywordsEnum = CFMLDeclaration.values();
		String[] keywords = new String[keywordsEnum.length];
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywordsEnum[i].getValue();
		}
		return keywords;
	}
}
