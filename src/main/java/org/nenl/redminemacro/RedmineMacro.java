package org.nenl.redminemacro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
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
	List<Issue> issues = new ArrayList<Issue>();
	private static final String MACRO_BODY_TEMPLATE = "templates/redmine-macro.vm";

  public RedmineMacro()
  {
	    String uri = "http://localhost/redmine";
	    String apiAccessKey = "68dc0004147a1c833e77b8ebb8a513448b97ae7e";
	    String projectKey = "testredmineproject";
	    Integer queryId = null; // any

    	issues = new ArrayList<Issue>();

    	RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, apiAccessKey);
    	IssueManager issueManager = mgr.getIssueManager();
    	
    	try
    	{
	    	issues = issueManager.getIssues(projectKey, queryId);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
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

	    Map<String, Object> context = MacroUtils.defaultVelocityContext();
    	
    	/*List<Object> information = new ArrayList<Object>();
    	
    	for(Issue issue : issues)
    	{
    		information.add(issue.getId());
    		information.add(issue.getSubject());
    	}*/
    	
    	context.put("issues", issues);
    	
	    return VelocityUtils.getRenderedTemplate(MACRO_BODY_TEMPLATE, context);
	  }
}