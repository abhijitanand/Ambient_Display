package tud.tk3.ambientdisplay.app.communication;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tud.tk3.ambientdisplay.app.display.Display;
import tud.tk3.ambientdisplay.app.display.DisplayTopology;
import tud.tk3.ambientdisplay.app.display.ImageSection;

/**
 * Created by simon on 5/21/14.
 */
public class DumbDisplayController implements DisplayController {

    private Map<String, Display> displays;

    @Override
    public void calculateAlignment(DisplayTopology dt) {
        displays = dt.displays;
        List<ImageSection> arranged = calculate(displays);
        System.out.println(arranged.toString());
    }

    private List<ImageSection> calculate(Map<String, Display> displays){
        ArrayList<ImageSection> sections = new ArrayList<ImageSection>();
        ArrayList<Display> sorted = new ArrayList<Display>(displays.values());
        Collections.sort(sorted);
        ArrayList<ImageSection> arrangedDisplays = new ArrayList<ImageSection>();

        Display display;
        int posX = 0;
        int posY = 0;
        int offsetX = 0;
        int offsetY = 0;
        for(int i = sorted.size() - 1; i >= 0; i--){
            display = sorted.get(i);
            offsetX = posX + display.width;
            offsetY = display.height;
            arrangedDisplays.add(new ImageSection(posX, posY, offsetX, offsetY));
            posX += display.width;
        }

        return arrangedDisplays;
    }

    public static void main(String[] args) {
        DisplayTopology dt = new DisplayTopology();
        dt.displays.put("1", new Display("1", 800, 600, 90));
        dt.displays.put("0", new Display("0", 1024, 768, 90));
        dt.displays.put("2", new Display("2", 640, 480, 90));

        DisplayController dc = new DumbDisplayController();
        dc.calculateAlignment(dt);
    }


}
