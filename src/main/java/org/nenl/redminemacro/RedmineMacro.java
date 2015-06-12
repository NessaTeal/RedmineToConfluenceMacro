package org.nenl.redminemacro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineMacro extends BaseMacro
{
	private static final String MACRO_BODY_TEMPLATE = "templates/redmine-macro.vm";

	private BandanaManager bandanaManager;
	
	public RedmineMacro(BandanaManager bandanaManager)
	{
		this.bandanaManager = bandanaManager;
	}
	
	public boolean isInline()
	{
		return false;
	}

	public boolean hasBody()
	{
		return false;
	}

	public RenderMode getBodyRenderMode()
	{
		return RenderMode.NO_RENDER;
	}
	
	@SuppressWarnings("rawtypes")
	public String execute(Map params, String body, RenderContext renderContext)
			throws MacroException
	{
		List<Issue> issues = new ArrayList<Issue>();
		
		String redmineHost = (String)bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT, "org.nenl.redminemacro.redminehost", false);
		
	    String uri = redmineHost;
	    String apiAccessKey = "68dc0004147a1c833e77b8ebb8a513448b97ae7e";
	    String projectKey = "";
	    Integer queryId = null;

	  	RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, null);
	  	IssueManager issueManager = mgr.getIssueManager();
	  	
	  	try
	  	{
		    issues = issueManager.getIssues(null, null);
	  	}
	  	catch(Exception e)
	  	{
	  		e.printStackTrace();
	  	}
	  	
	    Map<String, Object> context = MacroUtils.defaultVelocityContext();
	    
    	context.put("issues", issues);
    	
	    return VelocityUtils.getRenderedTemplate(MACRO_BODY_TEMPLATE, context);
	}
}