package tud.tk3.ambientdisplay.app.display;

import org.umundo.core.Message;

import java.util.HashMap;
import java.util.Map;

import tud.tk3.ambientdisplay.app.communication.Communicator;

/**
 * Created by martin on 21.05.14.
 */
public class DisplayTopology {
    public Map<String, Display> displays;

    public DisplayTopology() {
        displays = new HashMap<String, Display>();
    }
}
