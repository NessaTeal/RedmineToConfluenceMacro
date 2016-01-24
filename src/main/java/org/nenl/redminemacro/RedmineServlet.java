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
public class RedmineServlet extends HttpServlet {
  private final UserManager userManager;
  private final LoginUriProvider loginUriProvider;
  private final TemplateRenderer renderer;
  private final BandanaManager bandanaManager;

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
    String redmineHost = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
        "org.nenl.redminemacro.redminehost");

    if (request.getServletPath().contains("getHost")) {
      response.setHeader("redmineHost", redmineHost);
    } else {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)) {
        redirectToLogin(request, response);
        return;
      }

      String apiKey = (String) bandanaManager.getValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
          "org.nenl.redminemacro.apikey");

      Map<String, Object> context = new HashMap<String, Object>();
      context.put("redmineHost", redmineHost);
      context.put("apiKey", apiKey);

      response.setContentType("text/html;charset=utf-8");
      renderer.render("templates/admin.vm", context, response.getWriter());
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String username = userManager.getRemoteUsername(request);
    if (username != null && userManager.isSystemAdmin(username)) {
      bandanaManager.setValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
          "org.nenl.redminemacro.redminehost", request.getParameter("redmineHost"));
      bandanaManager.setValue(ConfluenceBandanaContext.GLOBAL_CONTEXT,
          "org.nenl.redminemacro.apikey", request.getParameter("apiKey"));
    }
  }

  private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
  }

  private URI getUri(HttpServletRequest request) {
    StringBuffer builder = request.getRequestURL();
    if (request.getQueryString() != null) {
      builder.append("?");
      builder.append(request.getQueryString());
    }
    return URI.create(builder.toString());
  }
}
