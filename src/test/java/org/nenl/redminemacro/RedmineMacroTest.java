package org.nenl.redminemacro;

import static org.junit.Assert.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;

public class RedmineMacroTest {

  @Mock
  String body;

  @Mock
  RenderContext renderContext;
  
  BandanaManager bandanaManager = Mockito.mock(BandanaManager.class);
  
  RedmineMacro tester = new RedmineMacro(bandanaManager);

  @Test
  public void testParseParams() {
    assertArrayEquals("JSON string should become 2-dimensional Java array",
        new String[] {"first", "second"},
        tester.parseParams("{\"ids\":[\"first\",\"second\"],\"fields\":[\"third\",\"fourth\"]}").get("ids"));

    assertArrayEquals("JSON string should become 2-dimensional Java array",
        new String[] {""},
        tester.parseParams("{\"ids\":[],\"fields\":[\"third\",\"fourth\"]}").get("ids"));
    
    assertArrayEquals("JSON string should become 2-dimensional Java array",
        new String[] {"third", "fourth"},
        tester.parseParams("{\"ids\":[\"first\",\"second\"],\"fields\":[\"third\",\"fourth\"]}").get("fields"));
    
    assertArrayEquals("JSON string should become 2-dimensional Java array",
        new String[] {""},
        tester.parseParams("{\"ids\":[\"first\",\"second\"],\"fields\":[]}").get("fields"));
  }

  @Test
  public void testExecute() throws MacroException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("Parameters", "{\"ids\":[],\"fields\":[]}");
    tester.execute(params, body, renderContext);
  }
}
