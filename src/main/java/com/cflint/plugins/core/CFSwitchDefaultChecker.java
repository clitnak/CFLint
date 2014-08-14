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
public class CFSwitchDefaultChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	//rule is: provide a default for switch statements to fall through
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equalsIgnoreCase("cfswitch")) {
			boolean isDefault = false;
			for (Element el : element.getChildElements()) {
				// decide if default was provided
				if (el.getName().equalsIgnoreCase("cfdefaultcase")) {
					// default found, so reassign and break
					isDefault = true;
					break;
				}
			}
			if (!isDefault) {	// no default found
			int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("NO_DEFAULT_INSIDE_SWITCH")
					.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
					.setMessage("Not having a default defined for a switch could pose potential issues")
					.build());
			}
		}
	}
}