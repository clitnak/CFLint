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
public class CommentChecker implements CFLintScanner {
	private static final int COMMENT_THRESHOLD = 4;
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {

	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	
	}

	public void element(final Element element, final Context context, final BugList bugs) {
		//try element.getContent() for commented code
		//or try StartTagType.Description property
		String elementName = element.getName();
		if (elementName.length() >= 3  && elementName.substring(0, 3).equals("!--")) {  //this includes cfml comments too
			int begLine = element.getSource().getRow(element.getBegin());
			int endLine = element.getSource().getRow(element.getEnd()); 
			int total = endLine - begLine;
			if (total > COMMENT_THRESHOLD) {
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("EXCESSIVE_COMMENT_LENGTH")
						.setSeverity(CFSeverity.INFO.getValue()).setFilename(context.getFilename())
						.setMessage("Comment is " + total + " lines. Could possibly be commented code, or comment is too long")
						.build());
			}
		}
	}
}