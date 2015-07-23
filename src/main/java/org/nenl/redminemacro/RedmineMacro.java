package org.nenl.redminemacro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.taskadapter.redmineapi.bean.Issue;

public class RedmineMacro extends BaseMacro {
  public class RedmineIssueGetter {
    public RedmineIssueGetter() {

    }

    public String get(Issue issue, String field) {
      String result = "";
      if (field.equals("ID"))
        result = issue.getId().toString();
      if (field.equals("Subject"))
        result = issue.getSubject();
      if (field.equals("Author"))
        result = issue.getAuthor().getFullName();
      if (field.equals("Created on"))
        result = issue.getCreatedOn().toString();
      if (field.equals("Description"))
        result = issue.getDescription();
      if (field.equals("Done ratio"))
        result = issue.getDoneRatio().toString();
      if (field.equals("Priority"))
        result = issue.getPriorityText();
      if (field.equals("Project"))
        result = issue.getProject().getName();
      if (field.equals("Start date"))
        result = issue.getStartDate().toString();
      if (field.equals("Status"))
        result = issue.getStatusName();
      if (field.equals("Tracker"))
        result = issue.getTracker().getName();
      if (issue.getCustomFieldByName(field) != null)
        result = issue.getCustomFieldByName(field).getValue();
      try {
        if (field.equals("Assignee"))
          result = issue.getAssignee().getFullName();
        if (field.equals("Category"))
          result = issue.getCategory().getName();
        if (field.equals("Due date"))
          result = issue.getDueDate().toString();
        if (field.equals("Estimated hours"))
          result = issue.getEstimatedHours().toString();
        if (field.equals("Spent hours"))
          result = issue.getSpentHours().toString();
      } catch (Exception e) {
        result = "Not set";
      }
      return result;
    }
  }

  private final String TEMPLATE = "templates/redmine-macro.vm";

  private BandanaManager bandanaManager;

  public RedmineMacro(BandanaManager bandanaManager) {
    this.bandanaManager = bandanaManager;
  }

  public boolean isInline() {
    return false;
  }

  public boolean hasBody() {
    return false;
  }

  public RenderMode getBodyRenderMode() {
    return RenderMode.NO_RENDER;
  }

  @SuppressWarnings("rawtypes")
  public String execute(Map params, String body, RenderContext renderContext)
      throws MacroException {

    Map<String, String[]> parameters = new HashMap<String, String[]>();

    parameters = parseParams((String) params.get("Parameters"));

    List<Issue> issues = new ArrayList<Issue>();

    String redmineHost = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
        "org.nenl.redminemacro.redminehost", false);

    issues = RedmineMacroUtil.getIssues(redmineHost, null, parameters.get("ids"));

    Map<String, Object> context = RedmineMacroUtil.getContext();

    context.put("fields", parameters.get("fields"));
    context.put("issues", issues);
    context.put("getter", new RedmineIssueGetter());

    return RedmineMacroUtil.renderTemplate(TEMPLATE, context);
  }

  Map<String, String[]> parseParams(String params) {
    String parameters = params.replace("\"", "");
    Map<String, String[]> answer = new HashMap<String, String[]>();
    answer.put("ids",
        parameters.substring(parameters.indexOf("[") + 1, parameters.indexOf("]")).split(","));
    answer.put("fields", parameters
        .substring(parameters.lastIndexOf("[") + 1, parameters.lastIndexOf("]")).split(","));
    return answer;
  }
}
