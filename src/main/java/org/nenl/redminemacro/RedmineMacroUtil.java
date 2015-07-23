package org.nenl.redminemacro;

import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;

public class RedmineMacroUtil implements RedmineMacroHelper {
	public RedmineMacroUtil() {
		
	}

	public Map<String, Object> getContext() {
		return MacroUtils.defaultVelocityContext();
	}
}
