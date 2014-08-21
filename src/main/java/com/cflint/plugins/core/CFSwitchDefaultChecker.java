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
public class CFSwitchDefaultChecker implements CFLintScanner {
	final String messageCode = "NO_DEFAULT_INSIDE_SWITCH";
	final String message = "Not having a default defined for a switch could pose potential issues";
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	//rule is: provide a default for switch statements to fall through
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equalsIgnoreCase(CFMLTag.CFSWITCH.getValue())) {
			boolean isDefault = false;
			for (Element el : element.getChildElements()) {
				// decide if default was provided
				if (el.getName().equalsIgnoreCase(CFMLTag.CFDEFAULTCASE.getValue())) {
					// default found, so reassign and break
					isDefault = true;
					break;
				}
			}
			if (!isDefault) {	// no default found
			int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode(messageCode)
					.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
					.setMessage(message)
					.build());
			}
		}
	}
}