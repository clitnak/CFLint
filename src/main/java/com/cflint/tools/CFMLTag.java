package com.cflint.tools;

public enum CFMLTag {
	CFFUNCTION("cffunction"),
	CFCOMPONENT("cfcomponent"),
	CFARGUMENT("cfargument"),
	CFOUTPUT("cfoutput"),
	CFQUERY("cfquery");
	
	private final String value;

	private CFMLTag(String value) {
	    this.value = value;
	}

	public String getName() {
	    return name();
	}

	public String getValue() {
	    return value;
	}
	
	public static String[] keywordValues() {
		CFMLTag[] keywordsEnum = CFMLTag.values();
		String[] keywords = new String[keywordsEnum.length];
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywordsEnum[i].getValue();
		}
		return keywords;
	}
}
