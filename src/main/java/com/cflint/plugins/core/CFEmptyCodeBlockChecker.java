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
public class CFEmptyCodeBlockChecker implements CFLintScanner {
	final String messageCode = "POSSIBLE_EMPTY_BLOCK_OF_CODE";
	final String message = "There seems to be a possible code block that is empty.";
		
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		
		
		if (element.getContent().isWhiteSpace() && element.getEndTag() != null) {
			int begLine = element.getSource().getRow(element.getBegin());
			bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode(messageCode)
				.setSeverity(CFSeverity.WARNING.getValue()).setFilename(context.getFilename())
				.setMessage(message)
				.build());
		}
	}
}