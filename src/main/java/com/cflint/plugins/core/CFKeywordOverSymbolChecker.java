package com.cflint.plugins.core;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFSeverity;

@Extension
public class CFKeywordOverSymbolChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		
		
		int begLine = element.getSource().getRow(element.getBegin());
		bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("USE_OF_KEYWORD_PREFERRED_OVER_SYMBOL")
			.setSeverity(CFSeverity.CAUTION.getValue()).setFilename(context.getFilename())
			.setMessage("Use of keyword comparision is preferred over a symbolic operator (ex: equals, not ==)")
			.build());
	}
}