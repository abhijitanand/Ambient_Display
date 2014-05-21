package tud.tk3.ambientdisplay.app.display;

/**
 * Created by martin on 21.05.14.
 */
public class Display {

    public String uid;

    public int width;
    public int height;
    public int dpi;


    public Display(String id, int width, int height, int dpi) {
        this.uid = id;
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }
}
