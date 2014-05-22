package tud.tk3.ambientdisplay.app.communication;



import android.graphics.Bitmap;

import java.util.ArrayList;
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
    private  AmbientDisplay ambientDisplay;

    public DumbDisplayController(AmbientDisplay ambientDisplay){
        this.ambientDisplay = ambientDisplay;
    }

    private class ConfigureTuple {
        List<ImageSection> arrangedDisplays;
        ImageSection imageSection;
    }

    @Override
    public void calculateAlignment(DisplayTopology dt) {
        displays = dt.displays;
        String myID = dt.myID;
        ConfigureTuple ct = calculate(displays, myID);
        ambientDisplay.topologyChange(ct.arrangedDisplays);
        ambientDisplay.imageSectionChange(ct.imageSection);
    }

    private ConfigureTuple calculate(Map<String, Display> displays, String myID){
        ConfigureTuple ct = new ConfigureTuple();
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
        double totalY = 0;
        // Calculate total width
        for(int i = sorted.size() - 1; i >= 0; i--){
            display = sorted.get(i);
            totalX += display.width / display.dpi;
            totalY = Math.max(totalY,display.height / display.dpi);
        }
        // Generate configuration
        for(int i = sorted.size() - 1; i >= 0; i--){
            display = sorted.get(i);
            offsetX = (display.width / (display.dpi * totalX));
            offsetY = display.height / (display.dpi * totalY);
            ImageSection section = new ImageSection(posX, posY, offsetX, offsetY);
            arrangedDisplays.add(section);
            if(display.uid.equals(myID)){
                ct.imageSection = section;
            }
            posX += display.width / (display.dpi * totalX);
        }

        ct.arrangedDisplays = arrangedDisplays;

        return ct;
    }

    public static void main(String[] args) {
        // Simple test case
        DisplayTopology dt = new DisplayTopology();
        dt.displays.put("1", new Display("1", 800, 600, 90));
        dt.displays.put("0", new Display("0", 1600, 1200, 90));
        dt.displays.put("2", new Display("2", 800, 400, 90));
        dt.myID = "1";

        DisplayController dc = new DumbDisplayController(new AmbientDisplay() {
            @Override
            public void displayImage(Bitmap image) {

            }

            @Override
            public void topologyChange(List<ImageSection> sections) {
                System.out.println(sections.toString());
            }

            @Override
            public void imageSectionChange(ImageSection section) {
                System.out.println(section.toString());
            }
        });
        dc.calculateAlignment(dt);
    }


}
