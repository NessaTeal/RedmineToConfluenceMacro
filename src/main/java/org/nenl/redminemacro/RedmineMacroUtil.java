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

public class RedmineMacroUtil {

  public static Map<String, Object> getContext() {
    return MacroUtils.defaultVelocityContext();
  }

  public static String renderTemplate(String template, Map<String, Object> context) {
    return VelocityUtils.getRenderedTemplate(template, context);
  }

  public static List<Issue> getIssues(String uri, String api, String[] ids) {
    IssueManager issueManager = RedmineManagerFactory.createWithApiKey(uri, api).getIssueManager();

    List<Issue> issues = new ArrayList<Issue>();
    try {
      for (String id : ids) {
        issues.add(issueManager.getIssueById(Integer.parseInt(id), Include.values()));
      }
    } catch (RedmineException e) {

    }
    return issues;
  }
}
