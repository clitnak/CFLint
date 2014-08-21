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
import com.cflint.tools.CFMLTag;

@Extension
public class CFInsertTagChecker implements CFLintScanner {
	final String messageCode = "AVOID_USING_" + CFMLTag.CFINSERT.getValue().toUpperCase() + "_TAG";
	final String message = "There are refresh, performance, and maintenance issues with the <"
						 + CFMLTag.CFINSERT.getValue().toUpperCase() + "> tag";
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		
	}
	
	//rule: do not use cfinsert tag 
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equalsIgnoreCase(CFMLTag.CFINSERT.getValue())){
			int begLine = element.getSource().getRow(element.getBegin());
			bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode(messageCode)
				.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
				.setMessage(message)
				.build());
			}
		}
	}