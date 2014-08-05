
package com.dictionary.out.tag;

import au.id.jericho.lib.html.EndTagType;
import au.id.jericho.lib.html.ParseText;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.StartTagTypeGenericImplementation;
import au.id.jericho.lib.html.Tag;

final class cfindex extends StartTagTypeGenericImplementation {
	protected static final cfindex INSTANCE = new cfindex();

	private cfindex() {
		super("CFML if tag", "<cfindex", ">", EndTagType.NORMAL, true, true, false);
	}

}
					