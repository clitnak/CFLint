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
public class CfqueryChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	//don't allow <cfquery> tag in a .cfm file
	public void element(final Element element, final Context context, final BugList bugs) {
		String file = context.getFilename();
		String ext = file.substring(file.length() - 3, file.length());
		String tagName = element.getName();
		if (tagName.equals("cfquery") && ext.equals("cfm")) {
			int begLine = element.getSource().getRow(element.getBegin());
			bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("NEVER_USE_QUERY_IN_CFM")
					.setSeverity(CFSeverity.ERROR.getValue()).setFilename(context.getFilename())
					.setMessage("Using a query in cfm is bad")
					.build());
		}
	}
}