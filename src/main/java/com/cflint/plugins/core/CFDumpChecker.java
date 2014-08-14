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
public class CFDumpChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		//look at component hint script style
	}
	//TODO add other types of logging commands that should not be in committed code
	//  can rename class if others are added
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equals("cfdump")) {
			int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("REMOVE_CFDUMP_TAG")
						.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
						.setMessage("Do not leave cfdump tags in committed code")
						.build());

		}
	}
}

			

				
			