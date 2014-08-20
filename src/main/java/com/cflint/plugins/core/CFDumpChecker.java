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
public class CFDumpChecker implements CFLintScanner {
	final String messageCode = "REMOVE_CFDUMP_TAG";
	final String message = "Do not leave cfdump tags in committed code";
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		//look at component hint script style
	}
	//TODO add other types of logging commands that should not be in committed code
	//  can rename class if others are added
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equals(CFMLTag.CFDUMP.getValue())) {
			int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode(messageCode)
						.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
						.setMessage(message)
						.build());

		}
	}
}

			

				
			