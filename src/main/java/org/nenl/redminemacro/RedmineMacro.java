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
    String apiKey = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
        "org.nenl.redminemacro.apikey", false);

    issues = RedmineMacroUtil.getIssues(redmineHost, apiKey, parameters.get("ids"));

    Map<String, Object> context = RedmineMacroUtil.getContext();

    context.put("fields", parameters.get("fields"));
    context.put("issues", issues);
    context.put("redmineMacroUtil", new RedmineMacroUtil());

    return RedmineMacroUtil.renderTemplate(TEMPLATE, context);
  }

  Map<String, String[]> parseParams(String params) {
    String parameters = params.replace("\"", "");
    Map<String, String[]> answer = new HashMap<String, String[]>();

    int indexOfIds = parameters.indexOf("ids:[") + 5;
    int indexOfFields = parameters.lastIndexOf("fields:[") + 8;
    answer.put("ids",
        parameters.substring(indexOfIds, parameters.indexOf("]", indexOfIds)).split(","));
    answer.put("fields",
        parameters.substring(indexOfFields, parameters.indexOf("]", indexOfFields)).split(","));
    return answer;
  }
}
