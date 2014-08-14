package com.cflint.plugins.core;

import java.util.Map;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFMLTag;
import com.cflint.tools.CFSeverity;

@Extension
public class FunctionOutputChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}
	
	//unsure if this rule in script style is valid- is railo default output="false" only on tag based functions?
	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		if (expression instanceof CFFuncDeclStatement) {
			CFFuncDeclStatement function = (CFFuncDeclStatement) expression;
			Map<String, String> attrs = function.getAttributes();
			if (attrs.get("output") != null && attrs.get("output").equals("false")) {
				final int begLine = function.getLine();
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("OUTPUT=FALSE_UNNECESSARY")
						.setSeverity("INFO").setFilename(context.getFilename())
						.setMessage("Output is set to false by default for functions")
						.build());
			}
		}
	}
	
	//rule is: dont use output="false" in cffunction
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equals(CFMLTag.CFFUNCTION.getValue())) {
			String output = element.getAttributeValue("output");
			if (output != null && (output.equals("false") || output.equals("no"))) { 
				int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("OUTPUT=FALSE_UNNECESSARY")
						.setSeverity(CFSeverity.INFO.getValue()).setFilename(context.getFilename())
						.setMessage("Output is set to false by default for functions")
						.build());
			}
		}
	}
}