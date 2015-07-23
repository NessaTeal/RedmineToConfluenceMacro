package org.nenl.redminemacro;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RedmineMacroUtil.class)
public class RedmineMacroTest {

  BandanaManager bandanaManager = Mockito.mock(BandanaManager.class);

  RedmineMacro tester = new RedmineMacro(bandanaManager);

  @Test
  public void testParseParams() {
    assertArrayEquals("JSON string should become 2-dimensional Java array",
        new String[] {"first", "second"},
        tester.parseParams("{\"ids\":[\"first\",\"second\"],\"fields\":[\"third\",\"fourth\"]}")
            .get("ids"));

    assertArrayEquals("JSON string should become 2-dimensional Java array", new String[] {""},
        tester.parseParams("{\"ids\":[],\"fields\":[\"third\",\"fourth\"]}").get("ids"));

    assertArrayEquals("JSON string should become 2-dimensional Java array",
        new String[] {"third", "fourth"},
        tester.parseParams("{\"ids\":[\"first\",\"second\"],\"fields\":[\"third\",\"fourth\"]}")
            .get("fields"));

    assertArrayEquals("JSON string should become 2-dimensional Java array", new String[] {""},
        tester.parseParams("{\"ids\":[\"first\",\"second\"],\"fields\":[]}").get("fields"));
  }

  @Test
  public void testExecute() throws MacroException {
    PowerMockito.mockStatic(RedmineMacroUtil.class);
    Mockito.when(RedmineMacroUtil.getContext()).thenReturn(new HashMap<String, Object>());
    Mockito.when(RedmineMacroUtil.renderTemplate(new String(), new HashMap<String, Object>()))
        .thenReturn("");
    Mockito.when(RedmineMacroUtil.getIssues(null, null, null)).thenReturn(null);

    Map<String, String> params = new HashMap<String, String>();
    params.put("Parameters", "{\"ids\":[],\"fields\":[]}");

    tester.execute(params, new String(), new RenderContext());

    PowerMockito.verifyStatic();
    RedmineMacroUtil.getIssues((String) any(), (String) any(), (String[]) any());
    PowerMockito.verifyStatic();
    RedmineMacroUtil.getContext();
    PowerMockito.verifyStatic();
    RedmineMacroUtil.renderTemplate(eq("templates/redmine-macro.vm"),
        anyMapOf(String.class, Object.class));
  }
}
