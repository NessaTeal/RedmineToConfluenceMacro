package org.nenl.redminemacro;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

@SuppressWarnings("serial")
//Servlet has three different actions:
//Show admin page where URL and API key may be changed
//Change URL and API key
//Return Redmine URL by request
public class RedmineServlet extends HttpServlet {
  private final UserManager userManager;
  private final LoginUriProvider loginUriProvider;
  private final TemplateRenderer renderer;
  private final BandanaManager bandanaManager;

  //All services that are required from Confluence
  public RedmineServlet(UserManager userManager, LoginUriProvider loginUriProvider,
      TemplateRenderer renderer, BandanaManager bandanaManager) {
    this.userManager = userManager;
    this.loginUriProvider = loginUriProvider;
    this.renderer = renderer;
    this.bandanaManager = bandanaManager;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
	//If user is not authorized then redirect to login
	String username = userManager.getRemoteUsername(request);
	if (username == null) {
      redirectToLogin(request, response);
      return;
    }
	
	//Get Redmine URL from BandanaManager as it is required in both possible actions
	//To return for request
	//Or to show on admin page
	String redmineHost = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
        "org.nenl.redminemacro.redminehost");
	
	  //If it is request for Redmine URL then
    if (request.getServletPath().contains("getHost")) {
    	//Set response header with required URL
    	response.setHeader("redmineHost", redmineHost);
    }
    else {
      //Get Redmine API key from BandanaManager to show it on admin page
      String apiKey = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
          "org.nenl.redminemacro.apikey");

      //Create empty context
      Map<String, Object> context = new HashMap<String, Object>();
      
      //Put there both URL and key
      context.put("redmineHost", redmineHost);
      context.put("apiKey", apiKey);

      //Set charset and render admin servlet
      response.setContentType("text/html;charset=utf-8");
      renderer.render("templates/admin.vm", context, response.getWriter());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
	//Get user
    String username = userManager.getRemoteUsername(request);
    
    //If it is user with admin privileges then update URL and key
    if (username != null && userManager.isSystemAdmin(username)) {
      bandanaManager.setValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
          "org.nenl.redminemacro.redminehost", request.getParameter("redmineHost"));
      bandanaManager.setValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
          "org.nenl.redminemacro.apikey", request.getParameter("apiKey"));
    }
    //Else redirect to login page
    else {
    	redirectToLogin(request, response);
    	return;
    }
  }
  
  //Redirect to login page of Confluence
  private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  //Create backward redirect to current page after login
  private URI getUri(HttpServletRequest request) {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null) {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }
}
