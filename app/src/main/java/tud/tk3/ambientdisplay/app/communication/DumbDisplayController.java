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
        double posX = 0;
        double posY = 0;
        double offsetX = 0;
        double offsetY = 0;
        double totalX = 0;
        // Calculate total width
        for(int i = sorted.size() - 1; i >= 0; i--){
            display = sorted.get(i);
            totalX += display.width / display.dpi;
        }
        double totalY = totalX / 1.6;
        // Generate configuration
        for(int i = sorted.size() - 1; i >= 0; i--){
            display = sorted.get(i);
            offsetX = (display.width / (display.dpi * totalX));
            offsetY = display.height / (display.dpi * totalY);
            arrangedDisplays.add(new ImageSection(posX, posY, offsetX, offsetY));
            posX += display.width / (display.dpi * totalX);
        }

        return arrangedDisplays;
    }

    public static void main(String[] args) {
        DisplayTopology dt = new DisplayTopology();
        dt.displays.put("1", new Display("1", 800, 600, 90));
        dt.displays.put("0", new Display("0", 1600, 1200, 90));
        dt.displays.put("2", new Display("2", 800, 400, 90));

        DisplayController dc = new DumbDisplayController();
        dc.calculateAlignment(dt);
    }


}
