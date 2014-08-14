package com.cflint.plugins.core;

import java.util.Map;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFCompDeclStatement;
import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFMLTag;
import com.cflint.tools.CFSeverity;

@Extension
public class HintChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		//clean up and reduce redundancy in two if branches
		if (expression instanceof CFFuncDeclStatement) {
			CFFuncDeclStatement function = (CFFuncDeclStatement) expression;
			Map<String, String> attrs = function.getAttributes();
			if (attrs.get("hint") == null) {
				int begLine = function.getLine();
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("HINT_STRONGLY_RECOMMENDED")
						.setSeverity("WARNING").setFilename(context.getFilename())
						.setMessage("Using a hint on functions makes it easier to understand code.")
						.build());
			}
		} else if (expression instanceof CFCompDeclStatement) {
			CFCompDeclStatement component = (CFCompDeclStatement) expression;
			Map<String, String> attrs = component.getAttributes();
			if (attrs.get("hint") == null) {
				int begLine = component.getLine();
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("HINT_STRONGLY_RECOMMENDED")
						.setSeverity("WARNING").setFilename(context.getFilename())
						.setMessage("Using a hint on components makes it easier to understand code.")
						.build());
			}
		}
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		String tagName = element.getName();
		if (tagName.equals(CFMLTag.CFFUNCTION.getValue()) || tagName.equals(CFMLTag.CFCOMPONENT.getValue())) {
			String hint = element.getAttributeValue("hint");
			if (hint == null) {

				int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("HINT_STRONGLY_RECOMMENDED")
						.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
						.setMessage("Using a hint is makes it easier to understand code.")
						.build());
			}
		}
	}
}