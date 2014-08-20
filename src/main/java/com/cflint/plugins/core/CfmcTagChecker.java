package com.cflint.plugins.core;

import java.util.HashMap;
import java.util.Map;

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
public class CfmcTagChecker implements CFLintScanner {
	
	
	public void expression(final CFExpression expression, final Context context, final BugList bugs) {
		
	}

	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
	
	}
	
	public void element(final Element element, final Context context, final BugList bugs) {
		/*cfmc tag replacements for: 
		 * cfmccheckbox(<input type="checkbox">) 
		 * cfmcdatePicker (<input type="date")
		 * cfmcimage (<input type="image">)
		 * cfmcTextField (<input type="text">)
		 * cfmcRadioButton (<input type="radio">)
		 * cfmcdropdown, cfmcSelectBox (<select>)
		 * cfmcdropdownGroup, "" "" (<optgroup>)
		 * cfmcdropdownOption, "" "" (<option>)
		 * cfmcFieldSet (<fieldset> inside <form> tags)
		 * cfmcTextArea (<textarea>)
		 
		 * (css and javascript excluded because already done. maybe add them all in one place)
		 * cfmcMinilist (<table> ?) cfmcMinilistColumn
		 * probably more that im missing in v11/server/mastercontrol/MCML/tags
		 */
		
		
		Map<String, String> htmlToCfmc = new HashMap<String, String>();
		htmlToCfmc.put("select", "<cfmcselectbox> or <cfmcdropdown>");				  	
		htmlToCfmc.put("optgroup", "<cfmcDropDownGroup> or <cfmcSelectBoxGroup>");
		htmlToCfmc.put("option", "<cfmcDropDownOption> or <cfmcSelectOption>");
		htmlToCfmc.put("fieldset", "<cfmcFieldSet>");
		htmlToCfmc.put("textarea", "<cfmcTextArea>");
		
		Map<String, String> inputTypes = new HashMap<String, String>();
		inputTypes.put("checkbox", "<cfmccheckbox>");
		inputTypes.put("date", "<cfmcdatepicker>");
		inputTypes.put("image", "<cfmcimage>");
		inputTypes.put("text", "<cfmcTextField>");
		inputTypes.put("radio", "<cfmcRadioButton>");
		
		String tagName = element.getName();
		//added for speed purposes-no html tags start with cf so dont even check them
		if (tagName.length() >= 2 && !tagName.substring(0, 2).equals("cf")) {
			if (tagName.equals("input")) {
				String type = element.getAttributeValue("type");
				for (String inputType : inputTypes.keySet()) {
					if (type != null && type.equals(inputType)) {
						int begLine = element.getSource().getRow(element.getBegin());
						bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("USE_CFMC_TAGS")
								.setSeverity(CFSeverity.ERROR.getValue()).setFilename(context.getFilename())
								.setMessage("HTML tag <input type=\"" + inputType + "\"> should be replaced with corresponding CFMC " 
											+ inputTypes.get(inputType) + " tag.")
								.build());
					}
				}
			} else {
				for (String htmlTag : htmlToCfmc.keySet()) {
					if (tagName.equals(htmlTag)) {
						int begLine = element.getSource().getRow(element.getBegin());
						bugs.add(new BugInfo.BugInfoBuilder().setLine(begLine).setMessageCode("USE_CFMC_TAGS")
								.setSeverity(CFSeverity.ERROR.getValue()).setFilename(context.getFilename())
								.setMessage("HTML tag <" + htmlTag + "> should be replaced with corresponding CFMC " 
											+ htmlToCfmc.get(htmlTag) + " tag")
								.build());
					}
				}
			}
		}
	}
}