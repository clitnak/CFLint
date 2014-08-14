package com.cflint.plugins.core;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFKeyword;
import com.cflint.tools.CFSeverity;

@Extension
public class CFKeywordLowercaseChecker implements CFLintScanner {
	
	String[] keywordsToCheckFor = CFKeyword.keywordValues();
		
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		int begLine = element.getSource().getRow(element.getBegin());
		
		String tag = element.getStartTag().toString();
		for (String s : keywordsToCheckFor) {
			if (tag.contains(s.toUpperCase())) {
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("KEYWORDS_SHOULD_BE_LOWERCASE")
					.setSeverity(CFSeverity.CAUTION.getValue()).setFilename(context.getFilename())
					.setMessage("Keyword \"" + s + "\" should be written with lowercase characters for consistency in code.")
					.build());
			}
		}
	}
}