package com.cflint.tools;

public enum CFKeyword {
	CONTAINS("contains"),
	CONTAIN("contain"),
	DOES("does"),
	IS("is"),
	GT("gt"),
	GE("ge"),
	GTE("gte"),
	LTE("lte"),
	LT("lt"),
	LE("le"),
	EQ("eq"),
	EQUAL("equal"),
	EQUALS("equals"),
	NEQ("neq"),
	LESS("less"),
	THAN("than"),
	GREATER("greater"),
	OR("or"),
	TO("to"),
	IMP("imp"),
	EQV("eqv"),
	XOR("xor"),
	AND("and"),
	NOT("not"),
	MOD("mod"),
	VAR("var"),
	NEW("new"),
	LOCAL("local"),
	VARIABLES("variables"),
	THIS("this"),
	ARGUMENTS("arguments"),
	NULL("null"),
	TRUE("true"),
	FALSE("false");

	private final String value;

	private CFKeyword(String value) {
	    this.value = value;
	}

	public String getName() {
	    return name();
	}

	public String getValue() {
	    return value;
	}
	
	public static String[] keywordValues() {
		CFKeyword[] keywordsEnum = CFKeyword.values();
		String[] keywords = new String[keywordsEnum.length];
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = keywordsEnum[i].getValue();
		}
		return keywords;
	}

}