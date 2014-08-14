package com.cflint.plugins.core;
import java.util.*;
import ro.fortsoft.pf4j.Extension;
import net.htmlparser.jericho.Element;
import cfml.parsing.cfscript.CFExpression;
import cfml.parsing.cfscript.script.CFScriptStatement;

import net.htmlparser.jericho.Tag;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.Source;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScanner;
import com.cflint.plugins.Context;
import com.cflint.tools.CFSeverity;

@Extension
public class CFScriptTagCountChecker implements CFLintScanner {
	List<String>violators = new ArrayList<String>();
	int count = 0;
	String fileName = "";
	final int limit = 5;
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		Source docSource = element.getSource();
		List<StartTag> cfScriptTags = docSource.getAllStartTags();
		fileName = context.getFilename();
		if (!violators.contains(fileName)){
			count = 0;
			violators.add(fileName);
			for (StartTag t : cfScriptTags){
				if (t.getName().equals("cfscript")){
					count++;
				}
			}
			if (count > limit){
				bugs.add(new BugInfo.BugInfoBuilder().setMessageCode("EXCESSIVE_CFSCRIPT_TAG_USE")
					.setSeverity(CFSeverity.INFO.getValue()).setFilename(context.getFilename())
					.setMessage("There are " + count + " cfscript tags in this file")
					.build());
					}
		}

	}
}
