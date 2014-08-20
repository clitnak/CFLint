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
public class ScriptTagChecker implements CFLintScanner {
	private static final int SCRIPT_LENGTH_THRESHOLD = 1;
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	
	}
	
	//rule: don't use inline javascript in cfm and cfc files
	public void element(final Element element, final Context context, final BugList bugs) {
		if (element.getName().equals("script")) {  
			int endLine = element.getSource().getRow(element.getEnd()); 
			int begLine = element.getSource().getRow(element.getBegin());
			int total = endLine - begLine;
			if (total > SCRIPT_LENGTH_THRESHOLD) {
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("AVOID_USING_INLINE_JS")
						.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
						.setMessage(element.getName() + " is too long, consider making it a <cfmcjavascript> tag")
						.build());
			}
		}
	}
}
