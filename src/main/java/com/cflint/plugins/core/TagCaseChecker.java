package com.cflint.plugins.core;

import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Tag;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFSeverity;

@Extension
public class TagCaseChecker implements CFLintScanner {
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		Tag startTag = element.getStartTag();
		String tagName = startTag.getNameSegment().toString();
		for(char c : tagName.toCharArray()){
			if (Character.isUpperCase(c)){
				int begLine = element.getSource().getRow(element.getBegin());
				bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("AVOID_CAPITALIZATION_IN_TAGS")
					.setSeverity(CFSeverity.INFO.getValue()).setFilename(context.getFilename())
					.setMessage("Tags should be all lower case.")
					.build());
				break;

			}

		}

	}
}
