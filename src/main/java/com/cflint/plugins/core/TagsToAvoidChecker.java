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
public class TagsToAvoidChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		
	}
	
	//rule: do not use cfupdate, cfinsert, or cfmodule tag 
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equalsIgnoreCase("cfupdate") || tagName.equalsIgnoreCase("cfinsert") || tagName.equalsIgnoreCase("cfmodule")){
			int begLine = element.getSource().getRow(element.getBegin());
			bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("AVOID_USING_"+tagName.toUpperCase()+"_TAG")
				.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
				.setMessage("Per MC standards, avoid using the <" + tagName + "> tag")
				.build());
			}
		}
	}
