package org.nenl.redminemacro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;

//Class with various utility functions
public class RedmineMacroUtil {

  public RedmineMacroUtil() {

  }
  
  //Moved here for testing purposes
  public static Map<String, Object> getContext() {
    return MacroUtils.defaultVelocityContext();
  }

  //Moved here for testing purposes
  public static String renderTemplate(String template, Map<String, Object> context) {
    return VelocityUtils.getRenderedTemplate(template, context);
  }

  //Return list of issues from Redmine
  public static List<Issue> getIssues(String uri, String api, String[] ids) {
	
	//Redmine's API IssueManager that retrieve information from server
    IssueManager issueManager = RedmineManagerFactory.createWithApiKey(uri, api).getIssueManager();

    //Initialize list
    List<Issue> issues = new ArrayList<Issue>();
    
    //Put issues by one into list
    try {
      for (String id : ids) {
        issues.add(issueManager.getIssueById(Integer.parseInt(id), Include.values()));
      }
    } catch (RedmineException e) {

    }
    return issues;
  }

  //Used in Velocity template to retrieve information
  //All posible options except custom fields are covered
  public String getField(Issue issue, String field) {
    String result = "";
    if (field.equals("ID"))
      result = issue.getId().toString();
    else if (field.equals("Subject"))
      result = issue.getSubject();
    else if (field.equals("Author"))
      result = issue.getAuthor().getFullName();
    else if (field.equals("Created on"))
      result = issue.getCreatedOn().toString();
    else if (field.equals("Description"))
      result = issue.getDescription();
    else if (field.equals("Done ratio"))
      result = issue.getDoneRatio().toString();
    else if (field.equals("Priority"))
      result = issue.getPriorityText();
    else if (field.equals("Project"))
      result = issue.getProject().getName();
    else if (field.equals("Start date"))
      result = issue.getStartDate().toString();
    else if (field.equals("Status"))
      result = issue.getStatusName();
    else if (field.equals("Tracker"))
      result = issue.getTracker().getName();
    else {
      try {
        if (field.equals("Assignee"))
          result = issue.getAssignee().getFullName();
        else if (field.equals("Category"))
          result = issue.getCategory().getName();
        else if (field.equals("Due date"))
          result = issue.getDueDate().toString();
        else if (field.equals("Estimated hours"))
          result = issue.getEstimatedHours().toString();
        else if (field.equals("Spent hours"))
          result = issue.getSpentHours().toString();
      } catch (Exception e) {
        result = "Not set";
      }
    }
    return result;
  }
}
