package com.cflint.plugins.core;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFMLTag;
import com.cflint.tools.CFSeverity;

@Extension
public class CFComponentChecker implements CFLintScanner {
	final String messageCode = "CFSCRIPT_FOR_COMPONENT_RECOMMENDED";
	final String message = "Components should be written in cfscript.";
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	//rule is: dont use output="false" in cffunction
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equals(CFMLTag.CFCOMPONENT.getValue())) {
			int begLine = element.getSource().getRow(element.getBegin());
			bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode(messageCode)
				.setSeverity(CFSeverity.INFO.getValue()).setFilename(context.getFilename())
				.setMessage(message)
				.build());
		}
	}
}