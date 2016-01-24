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

//This macro shows table with required issues and fields defined by JS counterpart
public class RedmineMacro extends BaseMacro {

  private final String TEMPLATE = "templates/redmine-macro.vm";

  private BandanaManager bandanaManager;

  //Macro require URL to Redmine and API key
  //Both stored within BandanaManager
  public RedmineMacro(BandanaManager bandanaManager) {
    this.bandanaManager = bandanaManager;
  }

  //Confluence-required function
  public boolean isInline() {
    return false;
  }
  
  //Confluence-required function
  public boolean hasBody() {
    return false;
  }
  
  //Confluence-required function
  public RenderMode getBodyRenderMode() {
    return RenderMode.NO_RENDER;
  }

  //Main method that executed when page with this macro is opened
  @SuppressWarnings("rawtypes")
  public String execute(Map params, String body, RenderContext renderContext)
      throws MacroException {


    //Parse parameters that are written inside macro parameters in JSON string
	Map<String, String[]> parameters = parseParams((String) params.get("Parameters"));

    //Get URL and API key of Redmine
    String redmineHost = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
        "org.nenl.redminemacro.redminehost", false);
    String apiKey = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
        "org.nenl.redminemacro.apikey", false);

    //Get list of issues from Redmine
    List<Issue> issues = RedmineMacroUtil.getIssues(redmineHost, apiKey, parameters.get("ids"));

    //Get default context
    Map<String, Object> context = RedmineMacroUtil.getContext();

    //Put chosen fields and corresponding information into context 
    context.put("fields", parameters.get("fields"));
    context.put("issues", issues);
    
    //Add our utility class
    context.put("redmineMacroUtil", new RedmineMacroUtil());

    //Render template with context
    return RedmineMacroUtil.renderTemplate(TEMPLATE, context);
  }

  //Convert parameters JSON string into map
  Map<String, String[]> parseParams(String params) {
	  
	//First get rid of all quotation marks
    String parameters = params.replace("\"", "");
    
    //Initialize empty map
    Map<String, String[]> answer = new HashMap<String, String[]>();

    //As string is also made by us it is possible to hard-code it
    int indexOfIds = parameters.indexOf("ids:[") + 5;
    int indexOfFields = parameters.lastIndexOf("fields:[") + 8;
    
    //Put into answer map all ids from opening bracket to closing one and substringing them by comma
    answer.put("ids",
        parameters.substring(indexOfIds, parameters.indexOf("]", indexOfIds)).split(","));
    
    //Same with fields
    answer.put("fields",
        parameters.substring(indexOfFields, parameters.indexOf("]", indexOfFields)).split(","));
    return answer;
  }
}
