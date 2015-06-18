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
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineMacro extends BaseMacro
{
	public class RedmineIssueGetter
	{
		public RedmineIssueGetter()
		{
			
		}
		
		public String get(Issue issue, String field)
		{
			String result = "";
			if(field.equals("ID"))
				result = issue.getId().toString();
			if(field.equals("Subject"))
				result = issue.getSubject();
			if(field.equals("Author"))
				result = issue.getAuthor().getFullName();
			if(field.equals("Created on"))
				result = issue.getCreatedOn().toString();
			if(field.equals("Description"))
				result = issue.getDescription();
			if(field.equals("Done ratio"))
				result = issue.getDoneRatio().toString();
			if(field.equals("Priority"))
				result = issue.getPriorityText();
			if(field.equals("Project"))
				result = issue.getProject().getName();
			if(field.equals("Start date"))
				result = issue.getStartDate().toString();
			if(field.equals("Status"))
				result = issue.getStatusName();
			if(field.equals("Tracker"))
				result = issue.getTracker().getName();
			if(issue.getCustomFieldByName(field) != null)
				result = issue.getCustomFieldByName(field).getValue();
			try
			{
				if(field.equals("Assignee"))
					result = issue.getAssignee().getFullName();
				if(field.equals("Category"))
					result = issue.getCategory().getName();
				if(field.equals("Due date"))
					result = issue.getDueDate().toString();
				if(field.equals("Estimated hours"))
					result = issue.getEstimatedHours().toString();
				if(field.equals("Spent hours"))
					result = issue.getSpentHours().toString();
			}
			catch(Exception e)
			{
				result = "Not set";
			}
			return result;
		}
	}
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
    	String parameters = (String)params.get("Parameters").toString().replace("\"", "");
    	
    	String ids[] = parameters.substring(parameters.indexOf("[") + 1, parameters.indexOf("]")).split(",");
    	String fields[] = parameters.substring(parameters.lastIndexOf("[") + 1, parameters.lastIndexOf("]")).split(",");
		
		List<Issue> issues = new ArrayList<Issue>();
		
		String redmineHost = (String)bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT, "org.nenl.redminemacro.redminehost", false);
		
	    String uri = redmineHost;

	  	RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri, null);
	  	IssueManager issueManager = mgr.getIssueManager();
	  	
	  	try
	  	{
	  		//issues = issueManager.getIssues(null, null);
	  		for(String id : ids)
	  			issues.add(issueManager.getIssueById(Integer.parseInt(id), Include.values()));
	  	}	
	  	catch(Exception e)
	  	{
	  		e.printStackTrace();
	  	}
	  	
	    Map<String, Object> context = MacroUtils.defaultVelocityContext();

	    context.put("fields", fields);
    	context.put("issues", issues);
    	context.put("getter", new RedmineIssueGetter());
    	
//    	String deb = "";
//    	
//    	for(String id : ids)
//    	{
//    		deb += id;
//    		deb += " ";
//    	}
//    	
//    	for(String field : fields)
//    	{
//    		deb += field;
//    		deb += " ";
//    	}
//    	
    	//return deb;
	    return VelocityUtils.getRenderedTemplate(MACRO_BODY_TEMPLATE, context);
	}
}