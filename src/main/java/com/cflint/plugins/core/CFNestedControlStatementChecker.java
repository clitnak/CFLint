
package com.cflint.plugins.core;

import java.util.List;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFMLControlStatement;
import com.cflint.tools.CFSeverity;

@Extension
public class CFNestedControlStatementChecker implements CFLintScanner {
	
	private int nestedLevel = 3; // deepest nested level allowed
	final String messageCode = "NESTED_CONTROL_STATEMENTS_TOO_DEEP";
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		for (String s : CFMLControlStatement.keywordValues()) {
			if (element.getName().equalsIgnoreCase(s)) {
				checkDepth(element, context, bugs);
			}
		}
	}
	
	private void checkDepth(final Element element, final Context context, final BugList bugs) {
		List<Element> children = element.getAllElements();
		for (Element tag : children) {
			int childDepthFromParent = tag.getDepth() - element.getDepth();
			if (childDepthFromParent > nestedLevel) {
				exceedsDepthAllowed(tag, childDepthFromParent, context, bugs);
			}
		}

	}
	
	private void exceedsDepthAllowed (final Element element, int depth, final Context context, final BugList bugs) {
		for (String s : CFMLControlStatement.keywordValues()) {
			if (element.getName().equalsIgnoreCase(s)) {
				int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode(messageCode)
					.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
					.setMessage("The element " + element.getName() + " is " + depth + " deep. This makes it hard to debug")
					.build());
				return;
			}
		}
	}
}